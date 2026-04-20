import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { AUTH_TOKEN_KEY, AUTH_USER_KEY, type CurrentUserInfo } from '../api/auth'
import { canAccessPagePermission, canAccessPath, getDefaultHomePath } from '../access/moduleAccess'
import AppLayout from '../layout/AppLayout.vue'
import { flatMenuItems } from './menu'
import { resolveMenuView } from './viewRegistry'

const moduleRoutes: RouteRecordRaw[] = flatMenuItems
  .map((item) => ({
    path: item.path.slice(1),
    name: item.path.slice(1).replace(/\//g, '-'),
    component: resolveMenuView(item.view),
    meta: item,
  }) as unknown as RouteRecordRaw)

const extraRoutes: RouteRecordRaw[] = [
  {
    path: 'customers/archive/create',
    name: 'customer-archive-create',
    component: () => import('../views/customer/CustomerArchiveView.vue'),
    meta: {
      title: '新增客户档案',
      pagePermission: '/customers',
    },
  },
  {
    path: 'customers/archive/:id',
    name: 'customer-archive-edit',
    component: () => import('../views/customer/CustomerArchiveView.vue'),
    meta: {
      title: '客户档案信息',
      pagePermission: '/customers',
    },
  },
  {
    path: 'hr/employees/create',
    name: 'hr-employee-create',
    component: () => import('../views/hr/EmployeeDetailView.vue'),
    meta: {
      title: '新增员工',
      pagePermission: '/hr/employees',
    },
  },
  {
    path: 'hr/employees/:id',
    name: 'hr-employee-detail',
    component: () => import('../views/hr/EmployeeDetailView.vue'),
    meta: {
      title: '员工详情',
      pagePermission: '/hr/employees',
    },
  },
  {
    path: 'loan-orders-self/:customerId',
    name: 'loan-orders-self-detail',
    component: () => import('../views/loan/LoanOrderSelfDetailView.vue'),
    meta: {
      title: '借贷详情（我方）',
      pagePermission: '/loan-orders-self',
    },
  },
  {
    path: 'loan-orders-bank/:customerId',
    name: 'loan-orders-bank-detail',
    component: () => import('../views/loan/LoanOrderBankDetailView.vue'),
    meta: {
      title: '借贷详情（银行）',
      pagePermission: '/loan-orders-bank',
    },
  },
  {
    path: 'increment-details/daily/:summaryId',
    name: 'increment-detail-daily',
    component: () => import('../views/loan/IncrementDailyDetailView.vue'),
    meta: {
      title: '日增量明细',
      pagePermission: '/increment-details',
    },
  },
  {
    path: 'opportunities/:id/follow',
    name: 'opportunity-follow',
    component: () => import('../views/opportunity/OpportunityFollowView.vue'),
    meta: {
      title: '商机跟进记录',
      pagePermission: '/opportunities',
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue'),
    },
    {
      path: '/',
      component: AppLayout,
      redirect: '/dashboard',
      children: [...moduleRoutes, ...extraRoutes],
    },
  ],
})

const readPersistedUser = (): CurrentUserInfo | null => {
  const rawUser = localStorage.getItem(AUTH_USER_KEY)
  if (!rawUser) {
    return null
  }
  try {
    return JSON.parse(rawUser) as CurrentUserInfo
  } catch {
    return null
  }
}

router.beforeEach((to) => {
  const token = localStorage.getItem(AUTH_TOKEN_KEY)
  const user = readPersistedUser()

  if (to.path !== '/login' && !token) {
    return '/login'
  }

  if (to.path === '/login' && token) {
    return getDefaultHomePath(user)
  }

  const metaPagePermission = typeof to.meta.pagePermission === 'string'
    ? to.meta.pagePermission
    : typeof to.meta.path === 'string'
      ? to.meta.path
      : undefined
  const requiresAdmin = Boolean(to.meta.requiresAdmin)
  const staffHome = Boolean(to.meta.staffHome)

  if (token && user && metaPagePermission && !canAccessPagePermission(metaPagePermission, user, { requiresAdmin, staffHome })) {
    return getDefaultHomePath(user)
  }

  if (token && user && !metaPagePermission && !canAccessPath(to.path, user)) {
    return getDefaultHomePath(user)
  }

  return true
})

export default router
