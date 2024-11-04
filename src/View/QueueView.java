public class LectureQueueView extends JPanel {
    private JTable queueTable;

    public LectureQueueView() {
        setLayout(new BorderLayout());
        queueTable = new JTable();
        add(new JScrollPane(queueTable), BorderLayout.CENTER);
    }

    // 대기열 상태를 표시하는 메서드
    public void showQueueStatus(ArrayList<QueueDTO> queueData) {
        String[] columnNames = { "강의명", "대기열 순위" };
        Object[][] data = new Object[queueData.size()][2];

        for (int i = 0; i < queueData.size(); i++) {
            data[i][0] = queueData.get(i).getLectureName();
            data[i][1] = queueData.get(i).getPosition();
        }

        queueTable.setModel(new DefaultTableModel(data, columnNames));
    }

    // 선택된 강의 정보를 반환
    public LectureDTO getSelectedLecture() {
        // 선택된 강의 정보 반환 로직 구현
        return null;  // 예시
    }
}
