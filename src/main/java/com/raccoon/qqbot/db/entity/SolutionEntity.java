package com.raccoon.qqbot.db.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author pyx
 * @since 2022-05-06
 */
@TableName("solution")
public class SolutionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "solution_id", type = IdType.AUTO)
    private Integer solutionId;

    private Integer problemId;

    private String userId;

    private String nick;

    private Integer time;

    private Integer memory;

    private LocalDateTime inDate;

    private Integer result;

    private Integer language;

    private String ip;

    private Integer contestId;

    private Byte valid;

    private Byte num;

    private Integer codeLength;

    private LocalDateTime judgetime;

    private BigDecimal passRate;

    private Integer lintError;

    private String judger;

    private String solutionUuid;

    public Integer getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Integer solutionId) {
        this.solutionId = solutionId;
    }
    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }
    public LocalDateTime getInDate() {
        return inDate;
    }

    public void setInDate(LocalDateTime inDate) {
        this.inDate = inDate;
    }
    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }
    public Byte getValid() {
        return valid;
    }

    public void setValid(Byte valid) {
        this.valid = valid;
    }
    public Byte getNum() {
        return num;
    }

    public void setNum(Byte num) {
        this.num = num;
    }
    public Integer getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(Integer codeLength) {
        this.codeLength = codeLength;
    }
    public LocalDateTime getJudgetime() {
        return judgetime;
    }

    public void setJudgetime(LocalDateTime judgetime) {
        this.judgetime = judgetime;
    }
    public BigDecimal getPassRate() {
        return passRate;
    }

    public void setPassRate(BigDecimal passRate) {
        this.passRate = passRate;
    }
    public Integer getLintError() {
        return lintError;
    }

    public void setLintError(Integer lintError) {
        this.lintError = lintError;
    }
    public String getJudger() {
        return judger;
    }

    public void setJudger(String judger) {
        this.judger = judger;
    }
    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    @Override
    public String toString() {
        return "SolutionEntity{" +
            "solutionId=" + solutionId +
            ", problemId=" + problemId +
            ", userId=" + userId +
            ", nick=" + nick +
            ", time=" + time +
            ", memory=" + memory +
            ", inDate=" + inDate +
            ", result=" + result +
            ", language=" + language +
            ", ip=" + ip +
            ", contestId=" + contestId +
            ", valid=" + valid +
            ", num=" + num +
            ", codeLength=" + codeLength +
            ", judgetime=" + judgetime +
            ", passRate=" + passRate +
            ", lintError=" + lintError +
            ", judger=" + judger +
            ", solutionUuid=" + solutionUuid +
        "}";
    }
}
