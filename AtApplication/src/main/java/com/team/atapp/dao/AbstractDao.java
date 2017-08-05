package com.team.atapp.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractDao {
	

    @PersistenceContext(unitName="AtappPU")
	private EntityManager entityManager;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
		
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void persist(Object entity){
		entityManager.persist(entity);
	}
	
	public Object update(Object entity){
		return entityManager.merge(entity);
	}
	
	
	public void delete(Object entity){
		entityManager.remove(entity);
	}
	public void deleteByIntId(Class clazz,int id){
		Object entity = entityManager.find(clazz, id);
		entityManager.remove(entity);
	}
	
	public void deleteByStringId(Class clazz,String id){
		Object entity = entityManager.find(clazz, id);
		entityManager.remove(entity);
	}
	
	public void flush(){
		entityManager.flush();
	}
	

	
}
