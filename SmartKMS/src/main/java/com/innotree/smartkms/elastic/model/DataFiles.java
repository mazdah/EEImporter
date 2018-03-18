package com.innotree.smartkms.elastic.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="datafiles")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DataFiles {
	
	@Id
	@Column(name="file_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int fileId;
	
	@Column(name="org_file_name")
	private String orgFileName;
	
	@Column(name="saved_file_name")
	private String savedFileName;
	
	@Column(name="file_size")
	private int fileSize;
	
	@Column(name="elastic_index")
	private String elasticIndex;
	
	@Column(name="elastic_type")
	private String elasticType;
	
	@Column(name="is_import")
	private boolean isImport;
	
	@Column(name="update_date")
	private Date updateDate;
	
	@Column(name="import_date")
	private Date importDate;

	public DataFiles(int fileId, String orgFileName, String savedFileName, int fileSize, String elasticIndex,
			String elasticType, boolean isImport, Date updateDate, Date importDate) {
		super();
		this.fileId = fileId;
		this.orgFileName = orgFileName;
		this.savedFileName = savedFileName;
		this.fileSize = fileSize;
		this.elasticIndex = elasticIndex;
		this.elasticType = elasticType;
		this.isImport = isImport;
		this.updateDate = updateDate;
		this.importDate = importDate;
	}

	public DataFiles() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
