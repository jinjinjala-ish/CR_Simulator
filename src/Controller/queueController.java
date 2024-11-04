public class LectureQueueController implements ActionListener {
    private LectureQueueView queueView;

    public LectureQueueController(LectureQueueView queueView) {
        this.queueView = queueView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String operator = ((JButton) obj).getText();
        switch (operator) {
            case Constants.ADD_TO_QUEUE_TXT:
                addToQueue();  // 대기열 등록 처리
                break;
            case Constants.VIEW_QUEUE_TXT:
                viewQueueStatus();  // 대기열 상태 조회
                break;
        }
    }

    // 대기열에 강의 등록
    private void addToQueue() {
        LectureDTO lecture = queueView.getSelectedLecture();
        if (lecture == null) {
            JOptionPane.showMessageDialog(null, "선택된 강의가 없습니다.");
            return;
        }

        boolean success = ClassManager.getInstance()
            .getDAO().addLectureToQueue(ClassManager.getInstance().getUser(), lecture);

        if (success) {
            JOptionPane.showMessageDialog(null, "대기열에 성공적으로 등록되었습니다.");
        } else {
            JOptionPane.showMessageDialog(null, "대기열 등록에 실패했습니다.");
        }
    }

    // 대기열 상태 조회
    private void viewQueueStatus() {
        ArrayList<QueueDTO> queueData = ClassManager.getInstance()
            .getDAO().getUserQueueStatus(ClassManager.getInstance().getUser());

        queueView.showQueueStatus(queueData);
    }
}
