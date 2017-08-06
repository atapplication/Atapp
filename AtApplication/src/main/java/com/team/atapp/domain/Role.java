package com.team.atapp.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name="roles")
@NamedQuery(name="Role.findAll", query="SELECT r FROM Role r")
public class Role implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String description;

	private String name;

	private String type;

	//bi-directional many-to-one association to AdminUser
	@OneToMany(mappedBy="role")
	private List<AdminUser> adminUsers;

	//bi-directional many-to-one association to TblUserInfo
	@OneToMany(mappedBy="roleBean")
	private List<TblUserInfo> tblUserInfos;

	public Role() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<AdminUser> getAdminUsers() {
		return this.adminUsers;
	}

	public void setAdminUsers(List<AdminUser> adminUsers) {
		this.adminUsers = adminUsers;
	}

	public AdminUser addAdminUser(AdminUser adminUser) {
		getAdminUsers().add(adminUser);
		adminUser.setRole(this);

		return adminUser;
	}

	public AdminUser removeAdminUser(AdminUser adminUser) {
		getAdminUsers().remove(adminUser);
		adminUser.setRole(null);

		return adminUser;
	}

	public List<TblUserInfo> getTblUserInfos() {
		return this.tblUserInfos;
	}

	public void setTblUserInfos(List<TblUserInfo> tblUserInfos) {
		this.tblUserInfos = tblUserInfos;
	}

	public TblUserInfo addTblUserInfo(TblUserInfo tblUserInfo) {
		getTblUserInfos().add(tblUserInfo);
		tblUserInfo.setRoleBean(this);

		return tblUserInfo;
	}

	public TblUserInfo removeTblUserInfo(TblUserInfo tblUserInfo) {
		getTblUserInfos().remove(tblUserInfo);
		tblUserInfo.setRoleBean(null);

		return tblUserInfo;
	}

}