export interface ModuleRouteMeta extends Record<string, unknown> {
  title: string
  description: string
  highlights: string[]
  pagePermission?: string
  stage?: string
  moduleKey?: string
  requiresAdmin?: boolean
  staffHome?: boolean
  collapsedByDefault?: boolean
}

export interface MenuItem extends ModuleRouteMeta {
  path: string
  icon: string
  view?: string
  children?: MenuItem[]
}

export interface MenuGroup {
  title: string
  icon?: string
  items: MenuItem[]
}
