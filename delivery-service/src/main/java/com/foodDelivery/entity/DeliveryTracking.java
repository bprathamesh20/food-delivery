package com.foodDelivery.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_tracking")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryTracking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "delivery_id", nullable = false)
	private Delivery delivery;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Delivery.DeliveryStatus statusUpdate;

	private String remarks;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime timestamp;

	public DeliveryTracking() {
	}

	// ---------- GETTERS & SETTERS ----------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Delivery.DeliveryStatus getStatusUpdate() {
		return statusUpdate;
	}

	public void setStatusUpdate(Delivery.DeliveryStatus statusUpdate) {
		this.statusUpdate = statusUpdate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
