package Tasks;

import java.util.Vector;

import Data.DataCache;
import Models.Person;
import Result.AllEventResult;
import Result.AllPersonResult;

public class GetDataTask {

    private final DataCache dc;

    public GetDataTask (){
        dc = DataCache.getInstance();
    }

    public void getPersonsAndEvents (){
        ServerProxy sp = new ServerProxy("10.0.2.2", "8081");
        AllPersonResult pr = sp.getAllPersons();
        dc.setAllPersons(pr.getData());

        AllEventResult er = sp.getAllEvents();
        dc.setAllEvents(er.getData());
        dc.setAllEvents(er.getData());
    }

}
