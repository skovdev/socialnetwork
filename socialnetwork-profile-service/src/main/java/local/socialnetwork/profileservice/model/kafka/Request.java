package local.socialnetwork.profileservice.model.kafka;

public class Request {

    private Object object;

    public Request(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
