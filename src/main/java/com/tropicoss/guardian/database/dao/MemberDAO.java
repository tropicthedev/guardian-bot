package com.tropicoss.guardian.database.dao;


import com.tropicoss.guardian.database.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberDAO {
    void addMember(Member member) throws SQLException;

    Member getMemberById(String memberId) throws SQLException;

    List<Member> getAllMembers() throws SQLException;

    void updateMember(Member member) throws SQLException;

    void deleteMember(String memberId) throws SQLException;
}
