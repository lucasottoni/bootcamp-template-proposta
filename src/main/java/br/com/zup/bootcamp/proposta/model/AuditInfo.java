package br.com.zup.bootcamp.proposta.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class AuditInfo {

    @Id
    private String id;

    @Column
    @NotNull
    private LocalDateTime auditTime;

    @Column
    @NotBlank
    private String userIp;

    @Column
    @NotBlank
    private String userAgent;

    protected AuditInfo() {
    }

    public AuditInfo(@NotBlank String userIp, @NotBlank String userAgent) {
        this.id = UUID.randomUUID().toString();
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.auditTime = LocalDateTime.now();
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public String getUserIp() {
        return userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
