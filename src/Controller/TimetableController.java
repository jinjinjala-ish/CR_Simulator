package Controller;

import common.ClassManager;
import Model.LectureDTO;
import Model.UserDTO;
import View.TimetableView;
import common.Constants;
import common.DesignConstants;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TimetableController Class
 * 메인 메뉴에서 '내 시간표' 버튼과 연결되어 실행되는 시간표 출력 Controller 클래스
 *
 * @author 이종진
 */
public class TimetableController implements ActionListener,MouseListener,AncestorListener {
    TimetableView timetableView;

    public TimetableController(TimetableView timetableView) {
        this.timetableView = timetableView;
    } // Constructor

    /**
     * drawTimetable Method
     * 수강신청 된 과목들의 시간을 요일과 시간별로 분할한 후, fillTimetableCell 메소드를 호출하여 시간표를 추가
     */
    private void drawTimetable() {
        timetableView.getTable().setModel(new DefaultTableModel(Constants.ROW_HEADER,Constants.COLUMN_HEADER));
        timetableView.getTable().getColumnModel().getColumn(0).setPreferredWidth(5);
        UserDTO user = ClassManager.getInstance().getMainMenuView().getUser();
        ArrayList<LectureDTO> lectureList =  ClassManager.getInstance().getDAO().getMyLecture(user);

        for (LectureDTO lecture : lectureList) {
            String strWeekAndTime = lecture.getTime();

            if (strWeekAndTime == null)
                continue; // 시간이 비어있는 경우 (보통 사이버강의) 다음 과목으로 continue

            String[] strSplitWeekAndTimeList = strWeekAndTime.split(", ");

            for (String strSplitWeekAndTime : strSplitWeekAndTimeList) {
                List<String> strSplitWeekList = new ArrayList<>(Arrays.asList(strSplitWeekAndTime.split(" ")));
                int listSize = strSplitWeekList.size();

                if (listSize < 2)
                    continue; // 형식에 맞지 않은 데이터가 들어가 있는 경우 continue

                // 리스트에서 시간만 따로 저장
                String time = strSplitWeekList.get(listSize - 1);
                strSplitWeekList.remove(listSize - 1);

                for (String week : strSplitWeekList)
                    fillTimetableCell(lecture.getClassName(), lecture.getClassRoom(), week, time);
            } // foreach
        } // foreach
    } // drawTimetable()

    /**
     * fillTimetableCell Method
     * 테이블 셀에 시간에 맞게 블럭 단위 과목 추가
     *
     * @param className : String
     * @param classRoom : String
     * @param strWeek : String
     * @param strTime : String
     */
    private void fillTimetableCell(final String className, final String classRoom, final String strWeek, final String strTime) {
        class TimeModel {
            int hour;
            int minute;

            TimeModel(String time) {
                String[] splitTime = time.split(":");
                hour = Integer.parseInt(splitTime[0]);
                minute = Integer.parseInt(splitTime[1]);
            } // Constructor

            int isHalf() { return minute > 0 ? 1 : 0; }
        } // TimeModel Value Object Inner Class

        String[] splitTimes = strTime.split("~");
        TimeModel startTime = new TimeModel(splitTimes[0]);
        TimeModel endTime = new TimeModel(splitTimes[1]);

        // 시간표가 추가 될 테이블 행의 시작 인덱스와 종료 인덱스를 계산하여 저장
        int startRowIndex = (startTime.hour - 9) * 2 + startTime.isHalf();
        int endRowIndex = (endTime.hour - 9) * 2 + endTime.isHalf();

        JTable table = timetableView.getTable();
        TableColumn column = table.getColumn(strWeek);
        int columnIndex = column.getModelIndex();

        table.setValueAt(className, startRowIndex, columnIndex); // 과목명으로 데이터 저장
        table.setValueAt(classRoom, startRowIndex + 1, columnIndex); // 강의실로 데이터 저장

        // 강의실이 표시된 셀 이후에 종료 시간 인덱스까지 공백 String으로 데이터 저장
        for (int i = startRowIndex + 2; i <= endRowIndex; ++i)
            table.setValueAt(" ", i, columnIndex);
    } // fillTimetableCell

    @Override
    public void actionPerformed(ActionEvent e) {
        String operator = ((JButton)e.getSource()).getText();
        switch (operator){
            case Constants.BACK_TXT:
                ClassManager.getInstance().getMain().comeToMain();
                break;
            default: //프린트
                try {
                    timetableView.getTable().print(); // 테이블 인쇄
                } catch (PrinterException ex) {  }
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JButton btnEvent = (JButton)e.getSource();
        if(btnEvent.getText() == Constants.BACK_TXT)
            btnEvent.setForeground(new Color(DesignConstants.HOVERING_COLOR));
        else
            timetableView.setHoveringPrintIcon(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JButton btnEvent = (JButton)e.getSource();
        if(btnEvent.getText() == Constants.BACK_TXT)
            btnEvent.setForeground(new Color(DesignConstants.SIGNATURE_COLOR));
        else
            timetableView.setHoveringPrintIcon(false);
    }

    @Override
    public void ancestorAdded(AncestorEvent event) { drawTimetable(); }

    @Override
    public void ancestorRemoved(AncestorEvent event) { }

    @Override
    public void ancestorMoved(AncestorEvent event) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }
} // TimetableController Class





public class FilterController implements ActionListener {
    private FilterView filterView;

    public FilterController(FilterView filterView) {
        this.filterView = filterView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String operator = ((JButton) obj).getText();
        switch (operator) {
            case Constants.SEARCH_TXT:
                searchFunction();
                break;
            case Constants.APPLY_LECTURE_TXT:
                applyLectureWithWarning();  // 신청 시 경고 및 보류 처리
                break;
            case Constants.EXIT_TXT:
                ClassManager.getInstance().getMain().comeToMain();
                break;
        }
    }

    // 강의 신청 시 취소석 여부 확인 및 경고 처리
    private void applyLectureWithWarning() {
        LectureDTO lecture = filterView.getSelectedLecture();
        if (lecture == null) {
            JOptionPane.showMessageDialog(null, "선택된 강의가 없습니다.");
            return;
        }

        // 서버로부터 해당 강의가 취소된 자리인지 여부 확인
        boolean isCancelledSeat = ClassManager.getInstance()
            .getDAO().isCancelledSeat(lecture.getCourseNum());

        if (isCancelledSeat) {
            int response = JOptionPane.showConfirmDialog(null, 
                "이 강의는 취소된 자리입니다. 신청을 진행하시겠습니까?", 
                "취소석 경고", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) return;  // 신청 취소
        }

        // 신청을 진행하며 임시 보류 처리 시작
        boolean success = ClassManager.getInstance()
            .getDAO().applyLectureWithHold(ClassManager.getInstance().getUser(), lecture);

        if (success) {
            JOptionPane.showMessageDialog(null, "수강 신청이 완료되었습니다.");
        } else {
            JOptionPane.showMessageDialog(null, "수강 신청에 실패했습니다. 다시 시도해주세요.");
        }
    }

    // 기존과 동일한 필터 검색 기능
    private void searchFunction() {
        // 필터 조건 JSON 생성 및 강의 목록 검색
        JSONObject jsonObject = new JSONObject();
        if (!filterView.getMajor().isEmpty())
            jsonObject.put(Constants.MAJOR_TXT, filterView.getMajor());

        ClassManager.getInstance().getMain()
            .changePanel(ClassManager.getInstance().getLectureListView(jsonObject));
    }
}


public class DAO {

    // 강의가 취소된 자리인지 서버에 확인
    public boolean isCancelledSeat(String courseNum) {
        String route = "lecture/cancelled/" + courseNum;
        JSONObject jsonObject = sendGet(Constants.BASE_URL + route);

        if (jsonObject == null) return false;
        return jsonObject.get(Constants.SUCCESS_TXT).equals(Constants.TRUE_TXT);
    }

    // 강의 신청 시 임시 보류 처리 포함
    public boolean applyLectureWithHold(UserDTO user, LectureDTO lecture) {
        String route = "reg/hold";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.ID_TXT, user.getId());
        jsonObject.put(Constants.COURSENUM_TXT, lecture.getCourseNum());
        jsonObject.put(Constants.CLASSNUM_TXT, lecture.getClassNum());

        jsonObject = sendRequest(Constants.BASE_URL + route, jsonObject.toString(), Constants.PUT_TXT);

        if (jsonObject == null) return false;
        return jsonObject.get(Constants.SUCCESS_TXT).equals(Constants.TRUE_TXT);
    }
}

