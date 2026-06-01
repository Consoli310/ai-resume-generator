package consoli.resume.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "resumes")
public class ResumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String generatedResumeJson;

    @Column(columnDefinition = "TEXT")
    private String htmlPreview;

    private String jobTitle;

    private String company;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private UserEntity user;

    public ResumeEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getGeneratedResumeJson() {
        return generatedResumeJson;
    }

    public void setGeneratedResumeJson(String generatedResumeJson) {
        this.generatedResumeJson = generatedResumeJson;
    }

    public String getHtmlPreview() {
        return htmlPreview;
    }

    public void setHtmlPreview(String htmlPreview) {
        this.htmlPreview = htmlPreview;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(
            UserEntity user
    ) {
        this.user = user;
    }
}