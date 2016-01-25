package ReadXml;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by CaoYu on 2015/12/11.
 */
public class DispatchOperation {
    private String Name;
    private String Vender;
    private String Receiver;
    private String GenTime;
    private ArrayList<TaskItems> taskItemsArrayList;
    private Task task;

    public void setGenTime(String genTime) {
        GenTime = genTime;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTaskItemsArrayList(ArrayList<TaskItems> taskItemsArrayList) {
        this.taskItemsArrayList = taskItemsArrayList;
    }

    public void setVender(String vender) {
        Vender = vender;
    }

    public ArrayList<TaskItems> getTaskItemsArrayList() {
        return taskItemsArrayList;
    }

    public String getName() {
        return Name;
    }

    public String getReceiver() {
        return Receiver;
    }

    public String getVender() {
        return Vender;
    }

    public Task getTask() {
        return task;
    }

    public String getGenTime() {
        return GenTime;
    }
}
