package servicestack.net.techstacks;

import net.servicestack.android.AndroidServiceClient;
import net.servicestack.client.AsyncResult;
import net.servicestack.client.Utils;

import servicestack.net.techstacks.dto.*;

import java.util.ArrayList;

public class App {

    public static App Instance = new App();

    AndroidServiceClient client;
    AppData appData;

    public static App get(){
        return Instance;
    }

    public static AppData getData(){
        return get().appData;
    }

    public App() {
        client = new AndroidServiceClient("http://techstacks.io");
        appData = new AppData(client);
    }

    public static class AppData
    {
        AndroidServiceClient client;

        ArrayList<AppDataListener> listeners = new ArrayList<>();

        public AppData addListener(AppDataListener callback){
            if (!listeners.contains(callback)){
                listeners.add(callback);
            }
            return this;
        }

        public AppData(AndroidServiceClient client){
            this.client = client;
        }

        public void onUpdate(DataType dataType){
            for (AppDataListener listener : listeners){
                listener.onUpdate(this, dataType);
            }
        }

        public AppData loadAppOverview(){
            if (appOverviewResponse != null){
                onUpdate(DataType.AppOverview);
            }
            client.getAsync(new AppOverview(), new AsyncResult<AppOverviewResponse>() {
                @Override
                public void success(AppOverviewResponse response){
                    appOverviewResponse = response;
                    onUpdate(DataType.AppOverview);
                }
            });
            return this;
        }

        AppOverviewResponse appOverviewResponse;
        public AppOverviewResponse getAppOverviewResponse(){
            return appOverviewResponse;
        }

        String lastTechStacksQuery = null;

        public AppData searchTechStacks(final String query){
            if (techStacksResponse != null && Utils.equals(query,lastTechStacksQuery)){
                onUpdate(DataType.SearchTechStacks);
            }

            lastTechStacksQuery = query;
            client.getAsync(new FindTechStacks(),
                    Utils.createMap("NameContains",query,"DescriptionContains",query),
                    new AsyncResult<QueryResponse<TechnologyStack>>() {
                        @Override
                        public void success(QueryResponse<TechnologyStack> response) {
                            if (Utils.equals(query,lastTechStacksQuery)){
                                techStacksResponse = response;
                                onUpdate(DataType.SearchTechStacks);
                            }
                        }
                    });

            return this;
        }

        QueryResponse<TechnologyStack> techStacksResponse;
        public QueryResponse<TechnologyStack> getSearchTechStacksResponse() {
            return techStacksResponse;
        }

        String lastTechnologiesQuery = null;

        public AppData searchTechnologies(final String query){
            if (technologiesResponse != null && Utils.equals(query,lastTechnologiesQuery)){
                onUpdate(DataType.SearchTechnologies);
            }

            lastTechnologiesQuery = query;
            client.getAsync(new FindTechnologies(),
                    Utils.createMap("NameContains",query,"DescriptionContains",query),
                    new AsyncResult<QueryResponse<Technology>>() {
                        @Override
                        public void success(QueryResponse<Technology> response) {
                            if (Utils.equals(query,lastTechnologiesQuery)){
                                technologiesResponse = response;
                                onUpdate(DataType.SearchTechnologies);
                            }
                        }
                    });

            return this;
        }

        QueryResponse<Technology> technologiesResponse;
        public QueryResponse<Technology> getSearchTechnologiesResponse() {
            return technologiesResponse;
        }
    }

    public static interface AppDataListener
    {
        public void onUpdate(AppData data, DataType dataType);
    }

    public static enum DataType
    {
        AppOverview,
        SearchTechStacks,
        SearchTechnologies,
    }


}