package com.huacai.customer.service;

import com.huacai.common.model.PageResponse;
import com.huacai.customer.dto.CustomerArchiveSaveRequest;
import com.huacai.customer.dto.CustomerContractSaveRequest;
import com.huacai.customer.dto.CustomerDebtSaveRequest;
import com.huacai.customer.dto.CustomerSaveRequest;
import com.huacai.customer.dto.CustomerScoreSaveRequest;
import com.huacai.customer.dto.CustomerTradeSaveRequest;
import com.huacai.customer.query.CustomerDebtPageQuery;
import com.huacai.customer.query.CustomerPageQuery;
import com.huacai.customer.query.CustomerRiskPageQuery;
import com.huacai.customer.vo.CustomerArchiveVO;
import com.huacai.customer.vo.CustomerContractVO;
import com.huacai.customer.vo.CustomerDebtVO;
import com.huacai.customer.vo.CustomerRiskVO;
import com.huacai.customer.vo.CustomerStatusLogVO;
import com.huacai.customer.vo.CustomerTradeVO;
import com.huacai.customer.vo.CustomerVO;
import java.util.List;

public interface CustomerService {

    PageResponse<CustomerVO> page(CustomerPageQuery query);

    CustomerVO detail(Long id);

    CustomerArchiveVO detailArchive(Long id);

    PageResponse<CustomerRiskVO> pageRisks(CustomerRiskPageQuery query);

    PageResponse<CustomerDebtVO> pageDebts(CustomerDebtPageQuery query);

    List<CustomerRiskVO> listRisksByCustomer(Long customerId);

    CustomerRiskVO getRisk(Long id);

    List<CustomerDebtVO> listDebtsByCustomer(Long customerId);

    CustomerDebtVO getDebt(Long id);

    List<CustomerContractVO> listContractsByCustomer(Long customerId);

    List<CustomerStatusLogVO> listStatusLogs(Long customerId);

    void create(CustomerSaveRequest request);

    void update(Long id, CustomerSaveRequest request);

    void createArchive(CustomerArchiveSaveRequest request);

    void updateArchive(Long id, CustomerArchiveSaveRequest request);

    void updateCustomerStatus(Long id, String status, String statusName);

    void createRisk(CustomerScoreSaveRequest request);

    void updateRisk(Long id, CustomerScoreSaveRequest request);

    void deleteRisk(Long id);

    void createDebt(CustomerDebtSaveRequest request);

    void updateDebt(Long id, CustomerDebtSaveRequest request);

    void deleteDebt(Long id);

    CustomerContractVO getContract(Long id);

    void createContract(Long customerId, CustomerContractSaveRequest request);

    void updateContract(Long id, CustomerContractSaveRequest request);

    void deleteContract(Long id);

    List<CustomerTradeVO> listTradesByCustomer(Long customerId);

    CustomerTradeVO getTrade(Long id);

    void createTrade(Long customerId, CustomerTradeSaveRequest request);

    void updateTrade(Long id, CustomerTradeSaveRequest request);

    void deleteTrade(Long id);
}
