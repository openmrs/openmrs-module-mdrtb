package org.openmrs.module.mdrtb;

import java.util.Date;

import org.openmrs.Order;
import org.openmrs.User;

public class OrderExtension {
    
    private Integer orderExtensionId;
    private Order order;
    private String value;
    private Boolean voided = false;
    private User voidedBy;
    private Date dateVoided;
    private String voidReason;
    private Date dateCreated;
    private User creator;
    
    public OrderExtension(){}
    public OrderExtension(Order o){
        this.order = o;
        this.voided = false;
        this.creator = o.getCreator();
        this.dateCreated = o.getDateCreated();
    }
    public OrderExtension(Order o, String value){
        this.order = o;
        this.voided = false;
        this.creator = o.getCreator();
        this.dateCreated = o.getDateCreated();
        this.value = value;
    }
    
    public Integer getOrderExtensionId() {
        return orderExtensionId;
    }
    public void setOrderExtensionId(Integer orderExtensionId) {
        this.orderExtensionId = orderExtensionId;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Boolean getVoided() {
        return voided;
    }
    public void setVoided(Boolean voided) {
        this.voided = voided;
    }
    public User getVoidedBy() {
        return voidedBy;
    }
    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }
    public Date getDateVoided() {
        return dateVoided;
    }
    public void setDateVoided(Date dateVoided) {
        this.dateVoided = dateVoided;
    }
    public String getVoidReason() {
        return voidReason;
    }
    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    
    
    
}
