package com.useroptix.odtool.to;

public class ProjectTo {
	private Long project_id;
	private String name;		
	private String org_name;		
	private String short_name;	
	private String owner_username;
	private Long org_id;
	public Long getProject_id() {
		return project_id;
	}
	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getShort_name() {
		return short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public String getOwner_username() {
		return owner_username;
	}
	public void setOwner_username(String owner_username) {
		this.owner_username = owner_username;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((org_id == null) ? 0 : org_id.hashCode());
		result = prime * result
				+ ((org_name == null) ? 0 : org_name.hashCode());
		result = prime * result
				+ ((owner_username == null) ? 0 : owner_username.hashCode());
		result = prime * result
				+ ((project_id == null) ? 0 : project_id.hashCode());
		result = prime * result
				+ ((short_name == null) ? 0 : short_name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectTo other = (ProjectTo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (org_id == null) {
			if (other.org_id != null)
				return false;
		} else if (!org_id.equals(other.org_id))
			return false;
		if (org_name == null) {
			if (other.org_name != null)
				return false;
		} else if (!org_name.equals(other.org_name))
			return false;
		if (owner_username == null) {
			if (other.owner_username != null)
				return false;
		} else if (!owner_username.equals(other.owner_username))
			return false;
		if (project_id == null) {
			if (other.project_id != null)
				return false;
		} else if (!project_id.equals(other.project_id))
			return false;
		if (short_name == null) {
			if (other.short_name != null)
				return false;
		} else if (!short_name.equals(other.short_name))
			return false;
		return true;
	}
}
