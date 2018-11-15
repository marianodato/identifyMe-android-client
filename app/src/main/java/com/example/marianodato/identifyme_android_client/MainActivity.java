package com.example.marianodato.identifyme_android_client;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.User;
import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.model.UserResults;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;
import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onFailureGenericLogic;
import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onResponseErrorGenericLogic;

public class MainActivity extends AppCompatActivity implements CommonKeys, ToolbarDialogFragment.NoticeDialogListener {

    private Menu menu;
    private ListView listView;
    private TextView emptyView;
    private UserAdapter userAdapter;
    private SwipeRefreshLayout swipeLayout;

    private static Handler handler;
    private static SharedPreferences prefs;
    private static UserService userService;
    private static List<User> list = new ArrayList<>();
    private static final int LIMIT = 10;
    private static final int SEARCH_DELAY = 1000;
    private static int total = 0;
    private static int offset = 0;
    private static String sortByOption;
    private static String filterByOption;
    private static String orderOption;
    private static String searchOption;
    private static boolean isLoadingList = false;

    private static final String FILTER_BY_SELECTED_INDEX_KEY = "filterBySelectedIndex";
    private static final String SORT_BY_SELECTED_INDEX_KEY = "sortBySelectedIndex";
    private static final String ORDER_SELECTED_INDEX_KEY = "orderSelectedIndex";

    private static final String DIALOG_TAG = "toolbarDialogFragment";
    private static final String DIALOG_TYPE_FILTER_BY = "filterBy";
    private static final String DIALOG_TYPE_SORT_BY = "sortBy";
    private static final String DIALOG_TYPE_ORDER = "order";

    private static final String[] SORT_BY_OPTIONS = {"id", "username", "name", "fingerprintId", "fingerprintStatus", "dni", "gender", "email", "phoneNumber", "isAdmin", "dateCreated", "lastUpdated"};
    private static final String[] FILTER_BY_OPTIONS = {null, STATUS_UNENROLLED, STATUS_PENDING, STATUS_ENROLLED};
    private static final String[] ORDER_OPTIONS = {"asc", "desc"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        filterByOption = FILTER_BY_OPTIONS[prefs.getInt(FILTER_BY_SELECTED_INDEX_KEY, 0)];
        sortByOption = SORT_BY_OPTIONS[prefs.getInt(SORT_BY_SELECTED_INDEX_KEY, 0)];
        orderOption = ORDER_OPTIONS[prefs.getInt(ORDER_SELECTED_INDEX_KEY, 0)];

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.USUARIOS));
        setSupportActionBar(toolbar);

        swipeLayout = findViewById(R.id.swipeContainer);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsersList();
            }
        });
        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAccent)
        );

        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);
        emptyView.setVisibility(View.GONE);
        userService = APIUtils.getUserService();
        handler = new Handler();

        getUsersList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsersList();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String dialogType, int selectedItemIndex) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        switch (dialogType) {
            case DIALOG_TYPE_FILTER_BY:
                editor.putInt(FILTER_BY_SELECTED_INDEX_KEY, selectedItemIndex);
                filterByOption = FILTER_BY_OPTIONS[selectedItemIndex];
                break;
            case DIALOG_TYPE_SORT_BY:
                editor.putInt(SORT_BY_SELECTED_INDEX_KEY, selectedItemIndex);
                sortByOption = SORT_BY_OPTIONS[selectedItemIndex];
                break;
            default:
                editor.putInt(ORDER_SELECTED_INDEX_KEY, selectedItemIndex);
                orderOption = ORDER_OPTIONS[selectedItemIndex];
                break;
        }
        editor.apply();
        getUsersList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String dialogType, int selectedItemIndex) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = this.getFragmentManager();
        DialogFragment toolbarDialogFragment = new ToolbarDialogFragment();
        Bundle args = new Bundle();

        final CharSequence[] SORT_BY_ITEMS = {getString(R.string.ID_SORT), getString(R.string.USUARIO_SORT), getString(R.string.NOMBRE_SORT), getString(R.string.ID_HUELLA_SORT), getString(R.string.ESTADO_HUELLA_SORT), getString(R.string.DNI_SORT), getString(R.string.GENERO_SORT), getString(R.string.EMAIL_SORT), getString(R.string.TELEFONO_SORT), getString(R.string.ADMIN_SORT), getString(R.string.CREADO_SORT), getString(R.string.MODIFICADO_SORT)};
        final CharSequence[] FILTER_BY_ITEMS = {getString(R.string.TODOS_FILTER), getString(R.string.NO_CARGADA_FILTER), getString(R.string.PENDIENTE_FILTER), getString(R.string.CARGADA_FILTER)};
        final CharSequence[] ORDER_ITEMS = {getString(R.string.ORDER_ASC), getString(R.string.ORDER_DESC)};

        switch (item.getItemId()) {

            case R.id.menuSearchUsers:
                SearchView mySearchView = (SearchView) item.getActionView();
                mySearchView.setQueryHint(getString(R.string.BUSCAR_USUARIOS));

                mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchOption = newText;
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUsersList();
                            }
                        }, SEARCH_DELAY);
                        return true;
                    }
                });

                mySearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewDetachedFromWindow(View arg0) {
                        // if search view is closed
                        menu.findItem(R.id.menuSortAndFilterUsers).setVisible(true);
                        menu.findItem(R.id.menuAddUser).setVisible(true);
                        menu.findItem(R.id.menuAbout).setVisible(true);
                        menu.findItem(R.id.menuLogout).setVisible(true);
                        searchOption = null;
                        getUsersList();
                    }

                    @Override
                    public void onViewAttachedToWindow(View arg0) {
                        // if search view is opened
                        menu.findItem(R.id.menuSortAndFilterUsers).setVisible(false);
                        menu.findItem(R.id.menuAddUser).setVisible(false);
                        menu.findItem(R.id.menuAbout).setVisible(false);
                        menu.findItem(R.id.menuLogout).setVisible(false);
                    }
                });
                break;

            case R.id.menuSortByUsers:
                args.putString(DIALOG_TYPE, DIALOG_TYPE_SORT_BY);
                args.putString(DIALOG_TITLE, getString(R.string.ORDENAR_POR));
                args.putCharSequenceArray(DIALOG_ITEMS, SORT_BY_ITEMS);
                args.putInt(DIALOG_SELECTED_INDEX, prefs.getInt(SORT_BY_SELECTED_INDEX_KEY, 0));
                toolbarDialogFragment.setArguments(args);
                toolbarDialogFragment.show(fragmentManager, DIALOG_TAG);
                break;

            case R.id.menuFilterByUsers:
                args.putString(DIALOG_TYPE, DIALOG_TYPE_FILTER_BY);
                args.putString(DIALOG_TITLE, getString(R.string.FILTRAR_POR));
                args.putCharSequenceArray(DIALOG_ITEMS, FILTER_BY_ITEMS);
                args.putInt(DIALOG_SELECTED_INDEX, prefs.getInt(FILTER_BY_SELECTED_INDEX_KEY, 0));
                toolbarDialogFragment.setArguments(args);
                toolbarDialogFragment.show(fragmentManager, DIALOG_TAG);
                break;

            case R.id.menuOrderUsers:
                args.putString(DIALOG_TYPE, DIALOG_TYPE_ORDER);
                args.putString(DIALOG_TITLE, getString(R.string.ORDEN));
                args.putCharSequenceArray(DIALOG_ITEMS, ORDER_ITEMS);
                args.putInt(DIALOG_SELECTED_INDEX, prefs.getInt(ORDER_SELECTED_INDEX_KEY, 0));
                toolbarDialogFragment.setArguments(args);
                toolbarDialogFragment.show(fragmentManager, DIALOG_TAG);
                break;

            case R.id.menuAddUser:
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra(IS_PUT_FORM, false);
                startActivity(intent);
                break;

            case R.id.menuAbout:
                Toast.makeText(MainActivity.this, getString(R.string.APP_VERSION), Toast.LENGTH_LONG).show();
                break;

            case R.id.menuLogout:
                doLogout();
                break;
        }
        return true;
    }

    private void removeAdapterItems() {
        list = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this, R.layout.list_user, list);
        listView.setAdapter(userAdapter);
    }

    private void loadNextData() {
        swipeLayout.setRefreshing(true);
        Call<UserResults> call = userService.getUsers(prefs.getString(ACCESS_TOKEN_KEY, null), offset, LIMIT, filterByOption, orderOption, sortByOption, searchOption);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(@NonNull Call<UserResults> call, @NonNull Response<UserResults> response) {
                if (response.isSuccessful()) {
                    total = response.body().getPaging().getTotal();
                    list = response.body().getResults();

                    for (int i = 0; i < list.size(); i++) {
                        userAdapter.add(list.get(i));
                    }

                    userAdapter.notifyDataSetChanged();
                } else {
                    onResponseErrorGenericLogic(MainActivity.this, response, true);
                    removeAdapterItems();
                    emptyView.setVisibility(View.VISIBLE);
                }
                isLoadingList = false;
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<UserResults> call, @NonNull Throwable t) {
                onFailureGenericLogic(MainActivity.this, t);
                removeAdapterItems();
                isLoadingList = false;
                swipeLayout.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getUsersList() {
        swipeLayout.setRefreshing(true);
        offset = 0;
        Call<UserResults> call = userService.getUsers(prefs.getString(ACCESS_TOKEN_KEY, null), offset, LIMIT, filterByOption, orderOption, sortByOption, searchOption);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(@NonNull Call<UserResults> call, @NonNull Response<UserResults> response) {
                if(response.isSuccessful()){
                    total = response.body().getPaging().getTotal();
                    list = response.body().getResults();
                    list.add(0, new User());
                    userAdapter = new UserAdapter(MainActivity.this, R.layout.list_user, list);
                    listView.setAdapter(userAdapter);
                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            // if end of list
                            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                                if (!isLoadingList) {
                                    int offsetCount = total / LIMIT;

                                    if (total % LIMIT != 0) {
                                        offsetCount++;
                                    }

                                    // if there is more information
                                    if (offset + 1 < offsetCount) {
                                        offset++;
                                        isLoadingList = true;
                                        loadNextData();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    onResponseErrorGenericLogic(MainActivity.this, response, true);
                    removeAdapterItems();
                    emptyView.setVisibility(View.VISIBLE);
                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<UserResults> call, @NonNull Throwable t) {
                onFailureGenericLogic(MainActivity.this, t);
                removeAdapterItems();
                swipeLayout.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void doLogout() {
        swipeLayout.setRefreshing(true);
        Call<UserLogin> call = userService.doLogout(prefs.getString(ACCESS_TOKEN_KEY, null));
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(@NonNull Call<UserLogin> call, @NonNull Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                    editor.remove(ACCESS_TOKEN_KEY);
                    editor.apply();
                    Toast.makeText(MainActivity.this, getString(R.string.SESION_FINALIZADA), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    onResponseErrorGenericLogic(MainActivity.this, response, true);
                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<UserLogin> call, @NonNull Throwable t) {
                onFailureGenericLogic(MainActivity.this, t);
                swipeLayout.setRefreshing(false);
            }
        });
    }
}
