package com.example.jjsminventoria;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import model.Users;

public class UserDBIntegrationTest {
    private DatabaseReference databaseReference;

    @Before
    public void setUp() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Test_Users");
    }

    @Test
    public void testConnection(){
        assertNotNull(databaseReference);
    }

    @Test
    public void testWriteAndReadData() throws InterruptedException {
        Users testUser = new Users(100,"kiwis","John Doe", "johndoe@gmail.com");
        CountDownLatch latch = new CountDownLatch(1);

        String userIdKey = "100";

        databaseReference.child(userIdKey).setValue(testUser).addOnSuccessListener(aVoid ->{
           databaseReference.child(userIdKey).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   Users retrievedUser = snapshot.getValue(Users.class);
                   assertNotNull(retrievedUser);
                   assertEquals(100, retrievedUser.getId());
                   assertEquals("John Doe", retrievedUser.getName());
//                   assertEquals("Employee", retrievedUser.getRole());
                   latch.countDown();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   fail("Database Read failed: " + error.getMessage());
                   latch.countDown();
               }
           });
        }).addOnFailureListener(e -> fail("Database write Failed: " + e.getMessage()));

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void findUserById() throws InterruptedException {
        int testUserId = 100;
        CountDownLatch latch = new CountDownLatch(1);

        databaseReference.child(String.valueOf(testUserId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users retrievedUser = snapshot.getValue(Users.class);
                    assertNotNull("User should not be null", retrievedUser);
                    assertEquals("User Id should match", testUserId, retrievedUser.getId());
                    assertEquals("User Name should match", "John Doe", retrievedUser.getName());
//                    assertEquals("Username should match", "TestUser", retrievedUser.getUsername());
                    assertEquals("User role should match", "Employee", retrievedUser.getRole());
                } else {
                    fail("User with ID " + testUserId + " does not exist");
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                fail("Database Read failed: " + error.getMessage());
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void deleteData() throws InterruptedException {
        int testUserId = 100;  // User ID to delete
        CountDownLatch latch = new CountDownLatch(1);

        // Check if the user exists first
        databaseReference.child(String.valueOf(testUserId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User exists, delete the user
                    databaseReference.child(String.valueOf(testUserId)).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Verify that the user is deleted
                                databaseReference.child(String.valueOf(testUserId)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        assertFalse("User should be deleted", snapshot.exists());
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        fail("Database Read failed: " + error.getMessage());
                                        latch.countDown();
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                fail("Failed to delete user: " + e.getMessage());
                                latch.countDown();
                            });
                } else {
                    fail("User with ID " + testUserId + " does not exist");
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                fail("Database Read failed: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait for the operation to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
