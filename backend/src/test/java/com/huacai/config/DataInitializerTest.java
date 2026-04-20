package com.huacai.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

class DataInitializerTest {

    @Test
    void applicationRunnerDelegatesToTransactionalService() throws Exception {
        DataInitializer dataInitializer = new DataInitializer();
        AdminDataInitializationService adminDataInitializationService = mock(AdminDataInitializationService.class);

        dataInitializer.applicationRunner(adminDataInitializationService).run(mock(ApplicationArguments.class));

        verify(adminDataInitializationService).initializeAdminData();
    }
}
