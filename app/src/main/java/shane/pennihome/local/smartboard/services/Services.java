package shane.pennihome.local.smartboard.services;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.services.interfaces.IService;

/**
 * Created by shane on 30/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Services extends ArrayList<IService> {
    public boolean hasService(IService service) {
        for (IService s : this)
            if (s.getName().equals(service.getName()))
                return true;
        return false;
    }

    public IService getByType(IService.ServicesTypes type)
    {
        for (IService s : this)
            if(s.getServiceType() == type)
                return s;
        return null;
    }

    public void remove(IService.ServicesTypes servicesTypes) {
        Services remove = new Services();
        for (IService s : this)
            if (s.getServiceType() == servicesTypes)
                remove.add(s);
        this.removeAll(remove);
    }
}
