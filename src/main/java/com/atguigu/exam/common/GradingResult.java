package com.atguigu.exam.common;

/**
 * 判卷结果内部类
 */
public  class GradingResult {

    private Integer score;
    private String feedback;
    private String reason;

    public GradingResult(Integer score, String feedback, String reason) {
        this.score = score;
        this.feedback = feedback;
        this.reason = reason;
    }

    // Getters
    public Integer getScore() { return score; }
    public String getFeedback() { return feedback; }
    public String getReason() { return reason; }
}