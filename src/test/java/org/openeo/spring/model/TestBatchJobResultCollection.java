package org.openeo.spring.model;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openeo.spring.loaders.JSONMarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for (de)serialization of batch job results of STAC type "Collection".
 */
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Transactional // automatic db rollback after each test
public class TestBatchJobResultCollection {
    
    @Autowired
    private SessionFactory sessionFactory; // cannot be static -> null is injected
    
    // single jdbc session for the whole suite
    private static SessionFactory sessionFactoryInstance;
    private static Session session;
    
    
    @BeforeEach
    public void setupEach() {
        if (null == session) {
            sessionFactoryInstance = sessionFactory;
            session = sessionFactory.openSession();
            System.out.println("JDBC Session created");
        }
        session.beginTransaction();
        session.getTransaction().setRollbackOnly(); // JIC
    }
    
    @AfterEach
    public void afterEach() {
      session.flush();
      session.clear();
      session.getTransaction().rollback();
    }
     
    @AfterAll
    public static void tearDown() {
        if (session != null) {
            session.clear();
            session.close();
            System.out.println("JDBC Session destroyed");
            sessionFactoryInstance.close();
            System.out.println("JDBC Session Factory destroyed");
        }
    }
    
    @Test
    public void deserializeJsonText_success()
            throws JsonMappingException, JsonProcessingException {
        
        String jsonText = JobResult.TEST_COLL.loadText();
        
        ObjectMapper OM = JSONMarshaller.MAPPER;
        BatchJobResultCollection res = OM.readValue(jsonText, BatchJobResultCollection.class);
        
        // assertions
        testFields(res);
    }
    
    @Test
    public void deserializeJsonTextViaObject_success()
            throws JsonMappingException, JsonProcessingException {
        
        String jsonText = JobResult.TEST_COLL.loadText();
        JSONObject responseJson = new JSONObject(jsonText);
        
        ObjectMapper OM = JSONMarshaller.MAPPER;
        BatchJobResultCollection res = OM.readValue(responseJson.toString(), BatchJobResultCollection.class);
        
        // assertions
        testFields(res);
    }
    
    @Test
    @Tag("IntegrationTest")
    @Transactional
    public void testCollectionPersistence() 
            throws JsonMappingException, JsonProcessingException {
        String jsonText = JobResult.TEST_COLL.loadText();     
        ObjectMapper OM = JSONMarshaller.MAPPER;
        BatchJobResultCollection coll = OM.readValue(jsonText, BatchJobResultCollection.class);
        
        // "Just-In-Case" automatic @Transactional cleanup verification
        BatchJobResultCollection dbColl0 = session.get(BatchJobResultCollection.class, coll.getId());
        assertNull(dbColl0);

        // persist to db
//        String collId = "a3cca2b2aa1e3b5b";
        String collId = (String) session.save(coll);
        session.flush(); // -> persist to db!
        session.clear();
        
        // recover and check
//        BatchJobResultCollection dbColl = session.createNativeQuery(
//                "select * from BATCHJOBRESULTCOLLECTION c where c.id = :collId", BatchJobResultCollection.class)
//                .setParameter("collId", collId)
//                .getSingleResult();
        BatchJobResultCollection dbColl = session.get(BatchJobResultCollection.class, collId);

//         assertEquals(coll, dbColl); // TODO empty lists versus null values may differ in&out of db
        assertEquals(coll.getTitle(), dbColl.getTitle());
        assertEquals(coll.getExtent(), dbColl.getExtent());
         // ...
    }
    
    /** Shared assertions for verify deserialization of {@link JobResult#TEST_COLL}. */
    private void testFields(BatchJobResultCollection coll) {
        assertEquals(coll.getId().toString(), "a3cca2b2aa1e3b5b");
        // ...
    }
}
