import { ref, reactive, type Ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 通用列表页 composable
 *
 * 封装了分页、关键字搜索、加载状态和数据刷新等列表页通用逻辑。
 * 各模块 Agent 在列表页中应优先使用此 composable，避免重复实现。
 *
 * @example
 * ```ts
 * const { pagination, loading, list, total, keyword, fetchList, handleSearch, handleReset, handlePageChange } =
 *   useListPage(async (params) => {
 *     const res = await getCustomerList(params)
 *     return { records: res.records, total: res.total }
 *   })
 * ```
 */

export interface PageParams {
  pageNum: number
  pageSize: number
  keyword: string
  [key: string]: unknown
}

export interface PageResult<T> {
  records: T[]
  total: number
}

export type FetchFn<T> = (params: PageParams) => Promise<PageResult<T>>

export interface UseListPageOptions {
  /** 默认每页条数，默认 20 */
  defaultPageSize?: number
  /** 是否在初始化时自动加载第一页，默认 true */
  immediate?: boolean
}

export function useListPage<T = unknown>(fetchFn: FetchFn<T>, options: UseListPageOptions = {}) {
  const { defaultPageSize = 20, immediate = true } = options

  const loading = ref(false)
  const list: Ref<T[]> = ref([]) as Ref<T[]>
  const total = ref(0)
  const keyword = ref('')

  const pagination = reactive({
    pageNum: 1,
    pageSize: defaultPageSize,
  })

  /** 额外的筛选参数，由调用方扩展 */
  const filters = reactive<Record<string, unknown>>({})

  const buildParams = (): PageParams => ({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    keyword: keyword.value.trim(),
    ...filters,
  })

  const fetchList = async () => {
    loading.value = true
    try {
      const result = await fetchFn(buildParams())
      list.value = result.records
      total.value = result.total
    } catch (error: unknown) {
      const msg = error instanceof Error ? error.message : '加载失败'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    pagination.pageNum = 1
    fetchList()
  }

  const handleReset = () => {
    keyword.value = ''
    Object.keys(filters).forEach((key) => {
      filters[key] = undefined
    })
    pagination.pageNum = 1
    fetchList()
  }

  const handlePageChange = (page: number) => {
    pagination.pageNum = page
    fetchList()
  }

  const handleSizeChange = (size: number) => {
    pagination.pageSize = size
    pagination.pageNum = 1
    fetchList()
  }

  if (immediate) {
    fetchList()
  }

  return {
    loading,
    list,
    total,
    keyword,
    pagination,
    filters,
    fetchList,
    handleSearch,
    handleReset,
    handlePageChange,
    handleSizeChange,
  }
}
