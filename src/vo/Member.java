package vo;


import lombok.Data;
import util.enumcollect.MemberEnum;

@Data
public class Member {
    private int memberSeq;
    private String memberId;
    private String memberPassword;
    private String memberName;
    private MemberEnum memberRole;



}
