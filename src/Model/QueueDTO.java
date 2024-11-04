public class QueueDTO {
    private String lectureName;
    private int position;

    public QueueDTO(JSONObject json) {
        this.lectureName = json.get("lectureName").toString();
        this.position = Integer.parseInt(json.get("position").toString());
    }

    public String getLectureName() {
        return lectureName;
    }

    public int getPosition() {
        return position;
    }
}
