# HR Module Handoff Documentation
**Date**: 2026-04-18
**Project**: 华彩系统1025
**Module**: HR (Human Resources) 人事管理

---

## 1. Implementation Summary

### 1.1 Scope Implemented

| Feature | Status | Description |
|---------|--------|-------------|
| Employee List (员工档案列表) | ✅ | Tabs for 在职/全部, search, CRUD operations |
| Employee Detail (员工详情) | ✅ | Full form with tabs for job info, certificates, assessments, etc. |
| My Profile (我的档案) | ✅ | Employee self-service profile viewing/editing |
| Salary Manage (工资管理) | ✅ | Salary standards CRUD |
| Transition Apply (转正申请) | ✅ | Probation → formal application workflow |
| Salary Adjust Apply (调薪申请) | ✅ | Salary adjustment with auto-calculated new salary |

### 1.2 Database Schema

Migration file: `sql/migration/V007_hr_module.sql`

Tables created:
- `hr_employee` - Core employee records
- `hr_employee_job_info` - Job/employment information
- `hr_employee_removal` - Employee removal/retirement records
- `hr_employee_certificate` - Certificates
- `hr_employee_assessment` - Assessment records
- `hr_employee_growth` - Work growth records
- `hr_employee_family` - Family members
- `hr_employee_change` - Employee change records
- `hr_employee_contract` - Labor contracts
- `hr_salary_standard` - Salary standards
- `hr_employee_salary` - Employee-salary mappings
- `hr_transition_apply` - Transition applications
- `hr_salary_adjust_apply` - Salary adjustment applications
- `hr_approval_record` - Approval records
- `hr_attachment` - Attachments
- `hr_management_record` - Management records
- `hr_leave_record` - Leave records

Permission items inserted into `sys_permission_item`:
- HR module pages: `/hr/employees`, `/hr/my-profile`, `/hr/salaries`
- Workflow pages: `/workflow/transitions`, `/workflow/salary-adjusts`
- Module keys: `hr.employees`, `hr.salaries`, `workflow.transitions`, `workflow.salary-adjusts`

---

## 2. Backend Structure

### 2.1 Package: `com.huacai.hr`

**Entities** (`entity/`):
- `HrEmployee`, `HrEmployeeJobInfo`, `HrEmployeeRemoval`, `HrEmployeeCertificate`
- `HrEmployeeAssessment`, `HrEmployeeGrowth`, `HrEmployeeFamily`
- `HrEmployeeChange`, `HrEmployeeContract`, `HrSalaryStandard`
- `HrEmployeeSalary`, `HrTransitionApply`, `HrSalaryAdjustApply`
- `HrApprovalRecord`, `HrAttachment`, `HrManagementRecord`, `HrLeaveRecord`

**Mappers** (`mapper/`): All extend `BaseMapper<T>`

**DTOs** (`dto/`):
- `EmployeeCreateRequest`, `EmployeeUpdateRequest`, `JobInfoSaveRequest`
- `RemovalSaveRequest`, `CertificateSaveRequest`, `AssessmentSaveRequest`
- `GrowthSaveRequest`, `FamilySaveRequest`, `ChangeSaveRequest`, `ContractSaveRequest`
- `SalaryStandardCreateRequest`, `SalaryStandardUpdateRequest`
- `EmployeeSalarySaveRequest`
- `TransitionApplyCreateRequest`, `TransitionApplyUpdateRequest`
- `SalaryAdjustApplyCreateRequest`, `SalaryAdjustApplyUpdateRequest`
- `ApprovalOpinionRequest`, `MyProfileUpdateRequest`

**VOs** (`vo/`):
- `EmployeeVO`, `EmployeeDetailVO`, `JobInfoVO`, `RemovalVO`, `CertificateVO`
- `AssessmentVO`, `GrowthVO`, `FamilyVO`, `ChangeVO`, `ContractVO`
- `SalaryStandardVO`, `EmployeeSalaryVO`
- `TransitionApplyVO`, `SalaryAdjustApplyVO`
- `ApprovalRecordVO`, `AttachmentVO`, `ManagementRecordVO`, `LeaveRecordVO`

**Controllers**:
- `HrController` at `/api/v1/hr/*`
- `WorkflowController` at `/api/v1/workflow/*`

**Service**: `HrManageServiceImpl`

### 2.2 Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/hr/employees` | Page employees |
| GET | `/hr/employees/{id}` | Employee detail |
| POST | `/hr/employees` | Create employee |
| PUT | `/hr/employees/{id}` | Update employee |
| DELETE | `/hr/employees/{id}` | Delete employee |
| GET | `/hr/employees/list` | List all employees |
| GET | `/hr/employees/{id}/job-info` | Get job info |
| POST | `/hr/job-info` | Save job info |
| GET | `/hr/employees/{id}/current-salary` | Get current salary |
| GET | `/hr/my-profile` | Get current user profile |
| PUT | `/hr/my-profile` | Update profile |
| GET | `/hr/salaries` | Page salary standards |
| POST | `/hr/salaries` | Create salary standard |
| PUT | `/hr/salaries/{id}` | Update salary standard |
| DELETE | `/hr/salaries/{id}` | Delete salary standard |
| GET | `/workflow/transitions` | Page transition applications |
| POST | `/workflow/transitions` | Create transition apply |
| PUT | `/workflow/transitions/{id}` | Update transition apply |
| POST | `/workflow/transitions/{id}/submit` | Submit for approval |
| POST | `/workflow/transitions/{id}/approve` | Approve |
| POST | `/workflow/transitions/{id}/reject` | Reject |
| GET | `/workflow/salary-adjusts` | Page salary adjust applies |
| POST | `/workflow/salary-adjusts` | Create salary adjust |
| PUT | `/workflow/salary-adjusts/{id}` | Update salary adjust |
| POST | `/workflow/salary-adjusts/{id}/submit` | Submit for approval |
| POST | `/workflow/salary-adjusts/{id}/approve` | Approve |
| POST | `/workflow/salary-adjusts/{id}/reject` | Reject |

### 2.3 Workflow Process

**Transition Apply** (转正申请):
- Status flow: DRAFT → PENDING_DEPT → PENDING_HR → PENDING_COMPANY → PENDING_ADMIN → APPROVED
- Rejected paths return to DRAFT

**Salary Adjust Apply** (调薪申请):
- Status flow: DRAFT → PENDING_DEPT → PENDING_HR → PENDING_LEADER → PENDING_SCHOOL → PENDING_FINANCE → APPROVED
- `newSalary` = `currentSalary` + `adjustAmount` (auto-calculated)
- Requires valid salary standard (checks `hr_employee_salary` with effective date)

### 2.4 System Account Creation

When creating employee with `createSystemAccount=1`:
1. Create `HrEmployee` record
2. Create `SysUser` with username, encrypted password
3. Assign default HR role if configured
4. Link user to employee via `hr_employee.user_id`

---

## 3. Frontend Structure

### 3.1 API Layer

**`src/api/hr.ts`**:
- `employeeApi` - Employee CRUD operations
- `myProfileApi` - Self-service profile
- `salaryApi` - Salary standard management

**`src/api/workflow.ts`**:
- `transitionApi` - Transition application workflow
- `salaryAdjustApi` - Salary adjustment workflow
- `workflowApi` - Shared workflow operations

### 3.2 Views

| View | Path | Description |
|------|------|-------------|
| EmployeeListView | `/hr/employees` | Employee list with tabs |
| EmployeeDetailView | `/hr/employees/:id`, `/hr/employees/create` | Employee detail/edit |
| MyProfileView | `/hr/my-profile` | Self-service profile |
| SalaryManageView | `/hr/salaries` | Salary standards CRUD |
| TransitionApplyView | `/workflow/transitions` | Transition applications |
| SalaryAdjustApplyView | `/workflow/salary-adjusts` | Salary adjustments |

### 3.3 Menu Configuration

Updated `src/router/menu.ts` with "人事管理" section containing:
- 员工档案 `/hr/employees`
- 我的档案 `/hr/my-profile`
- 工资管理 `/hr/salaries`
- 转正申请 `/workflow/transitions`
- 调薪申请 `/workflow/salary-adjusts`

### 3.4 Router Configuration

Added extra routes in `src/router/index.ts`:
- `/hr/employees/create` → `EmployeeDetailView`
- `/hr/employees/:id` → `EmployeeDetailView`
- `/workflow/transitions` → `TransitionApplyView`
- `/workflow/salary-adjusts` → `SalaryAdjustApplyView`

---

## 4. Security Integration

Updated `ModuleAccessRegistry.java`:

**Page Permissions**:
- `/hr/employees` - Employee management
- `/hr/my-profile` - Personal profile
- `/hr/salaries` - Salary management
- `/workflow/transitions` - Transition apply
- `/workflow/salary-adjusts` - Salary adjust

**Module Keys**:
- `hr.employees`
- `hr.salaries`
- `workflow.transitions`
- `workflow.salary-adjusts`

**API Endpoints** (for button permissions):
- `/api/v1/hr/*` - HR endpoints
- `/api/v1/workflow/transitions/*` - Transition endpoints
- `/api/v1/workflow/salary-adjusts/*` - Salary adjust endpoints

---

## 5. Build Verification

- [x] Frontend: `npm run build` passes with no TypeScript errors
- [ ] Backend: Maven compile (mvn not available in environment)

---

## 6. Known Issues / Notes

1. **Backend compilation not verified**: Maven not installed in current environment. Recommend running `./mvnw compile` or `mvn compile` before deployment.

2. **Import/Export buttons**: Disabled in EmployeeListView (marked as "功能开发中")

3. **Job Info tab**: Displays but save functionality not fully implemented in EmployeeDetailView

4. **Employee photos**: `idPhotoUrl` field exists but no upload UI implemented

---

## 7. API-to-View Mapping

| Frontend View | API Used | Key Data Flow |
|--------------|----------|---------------|
| EmployeeListView | `employeeApi.page()` | List with filters → table |
| EmployeeDetailView | `employeeApi.detail()`, `employeeApi.create/update()` | Form ↔ API |
| MyProfileView | `myProfileApi.get()`, `myProfileApi.update()` | Self-service profile |
| SalaryManageView | `salaryApi.page()`, `salaryApi.create/update/delete()` | Salary standards CRUD |
| TransitionApplyView | `transitionApi.page()`, `transitionApi.create/update/submit()` | Workflow apply |
| SalaryAdjustApplyView | `salaryAdjustApi.page()`, `salaryAdjustApi.create/update/submit()` | Salary adjust workflow |

---

## 8. Next Steps for Verification

1. Run backend compile: `cd backend && ./mvnw compile`
2. Start backend: `./mvnw spring-boot:run`
3. Start frontend: `cd frontend && npm run dev`
4. Test employee creation with system account
5. Test transition workflow (create → submit → approve/reject)
6. Test salary adjust workflow with salary standard lookup
7. Verify permission model integration
