package com.oceanview.reservation.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private Integer userId; // nullable
    private String action;
    private String entityType;
    private Integer entityId; // nullable for some actions
    private String oldValue; // JSON string
    private String newValue; // JSON string
    private LocalDateTime actionTime;
    private String ipAddress;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getActionTime() { return actionTime; }
    public void setActionTime(LocalDateTime actionTime) { this.actionTime = actionTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
