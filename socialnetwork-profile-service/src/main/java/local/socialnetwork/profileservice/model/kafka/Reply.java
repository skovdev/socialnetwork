package local.socialnetwork.profileservice.model.kafka;

public class Reply {

    private Object object;

    public Reply(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
