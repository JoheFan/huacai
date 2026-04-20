const viewModules = import.meta.glob('../views/**/*.vue')

const fallbackView = () => import('../views/common/ModuleView.vue')

export const resolveMenuView = (view: string | undefined) => {
  if (!view) {
    return fallbackView
  }

  const modulePath = `../views/${view}.vue`
  return viewModules[modulePath] ?? fallbackView
}
