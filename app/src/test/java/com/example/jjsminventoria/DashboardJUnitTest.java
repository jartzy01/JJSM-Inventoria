package com.example.jjsminventoria;

import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.example.jjsminventoria.ui.dashboard.DashboardViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import model.Products;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DashboardJUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseConnection firebaseConnectionMock;

    private DashboardViewModel viewModel;

    private List<Products> fakeProductList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Fake data
        Products product1 = new Products("1", "Laptop", 10, 999.99);
        Products product2 = new Products("2", "Mouse", 0, 19.99); // mouse out of stock
        fakeProductList = Arrays.asList(product1, product2);

        // Inject mock
        viewModel = new DashboardViewModel(firebaseConnectionMock);
    }

    @Test
    public void testSetAllProducts_correctCounts() {
        // manually call setAllProducts with fake data
        viewModel.setAllProducts(fakeProductList);

        // verify LiveData values
        assertEquals(Integer.valueOf(10), viewModel.getInventoryQty().getValue()); // 10 items in total
        assertEquals(Integer.valueOf(2), viewModel.getTotalProducts().getValue()); // 2 products
        assertEquals(Integer.valueOf(1), viewModel.getOutOfStockCount().getValue()); // 1 out of stock
        assertEquals(Integer.valueOf(1), viewModel.getLowStockCount().getValue()); // 1 low stock (Laptop with qty 10)
    }
}
