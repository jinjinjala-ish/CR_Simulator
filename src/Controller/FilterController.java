package Controller;

import common.ClassManager;
import View.FilterView;
import common.Constants;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FilterController Class
 * Filter후 강의를 출력하는 클래스입니다.
 */

public class FilterController implements ActionListener {
    private FilterView filterView;

    public FilterController(FilterView realFilter)  {
        this.filterView = realFilter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String operator = ((JButton)obj).getText();
        switch (operator){
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
    
    
    //필터를 정리 후 서치를 하는 메소드
    private void searchFunction(){
        JSONObject jsonObject = new JSONObject();
        if(!filterView.getMajor().isEmpty())
            jsonObject.put(Constants.MAJOR_TXT,filterView.getMajor());
        if(!filterView.getCourseNum().isEmpty())
            jsonObject.put(Constants.COURSENUM_TXT,filterView.getCourseNum());
        if(!filterView.getClassName().isEmpty())
            jsonObject.put(Constants.CLASSNAME_TXT,filterView.getClassName());
        if(!filterView.getProfessor().isEmpty())
            jsonObject.put(Constants.PROFESSOR_TXT,filterView.getProfessor());
        if(!filterView.getGrade().isEmpty())
            jsonObject.put(Constants.GRADE_TXT,filterView.getGrade());
        if(!filterView.getClassNum().isEmpty())
            jsonObject.put(Constants.CLASSNUM_TXT,filterView.getClassNum());
        if(!filterView.getCompletion().isEmpty())
            jsonObject.put(Constants.COMPLETION_TXT,filterView.getCompletion());
        ClassManager.getInstance().getMain().changePanel(ClassManager.getInstance().getLectureListView(jsonObject));
    }
} // RealFilterController Class
