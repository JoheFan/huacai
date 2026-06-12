<script setup lang="ts">
defineProps<{
  name: string
  size?: number | string
}>()
</script>

<template>
  <svg
    :width="size ?? 20"
    :height="size ?? 20"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.85"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <!-- logo: hexagonal platform icon -->
    <template v-if="name === 'logo'">
      <path d="M12 2 20 6.5v11L12 22 4 17.5v-11L12 2Z" fill="currentColor" opacity=".2"/>
      <path d="M12 2 20 6.5v11L12 22 4 17.5v-11L12 2Z"/>
      <path d="M8 10.2 12 12.5 16 10.2M8 13.4 12 15.7 16 13.4"/>
    </template>

    <!-- user: person icon -->
    <template v-else-if="name === 'customer'">
      <circle cx="12" cy="8" r="3.4"/>
      <path d="M5.3 18.2c1.4-3 4-4.6 6.7-4.6s5.3 1.6 6.7 4.6"/>
    </template>

    <!-- user: same as customer (alias) -->
    <template v-else-if="name === 'user'">
      <circle cx="12" cy="8" r="3.4"/>
      <path d="M5.3 18.2c1.4-3 4-4.6 6.7-4.6s5.3 1.6 6.7 4.6"/>
    </template>

    <!-- risk: alert triangle -->
    <template v-else-if="name === 'risk'">
      <path d="M10.3 5.8 3 17.5h18L13.7 5.8a2 2 0 0 0-3.4 0Z"/>
      <path d="M12 10v3M12 15.5v.5"/>
    </template>

    <!-- briefcase: business icon -->
    <template v-else-if="name === 'briefcase'">
      <rect x="4.2" y="7.2" width="15.6" height="11.8" rx="2.6"/>
      <path d="M9 7.2v-1c0-1 .8-1.8 1.8-1.8h2.4c1 0 1.8.8 1.8 1.8v1"/>
      <path d="M4.2 11h15.6"/>
    </template>

    <!-- chart: bar chart with trend -->
    <template v-else-if="name === 'chart'">
      <path d="M5 18.5h14"/>
      <path d="M7.5 16V11"/>
      <path d="M12 16V8"/>
      <path d="M16.5 16V6"/>
      <path d="M7.5 8.5 12 6.5l4.5-2"/>
    </template>

    <!-- person-badge: HR icon -->
    <template v-else-if="name === 'personBadge'">
      <circle cx="12" cy="7.8" r="3.1"/>
      <path d="M6.1 18.1c1.4-2.9 3.8-4.5 5.9-4.5s4.5 1.6 5.9 4.5"/>
    </template>

    <!-- coins: finance icon -->
    <template v-else-if="name === 'coins'">
      <ellipse cx="12" cy="7" rx="5.8" ry="2.5"/>
      <path d="M6.2 7v3.7c0 1.4 2.6 2.5 5.8 2.5s5.8-1.1 5.8-2.5V7"/>
      <path d="M6.2 10.7v3.6c0 1.4 2.6 2.5 5.8 2.5s5.8-1.1 5.8-2.5v-3.6"/>
    </template>

    <!-- approval: stamp icon -->
    <template v-else-if="name === 'approval'">
      <circle cx="12" cy="7.4" r="2.8"/>
      <path d="M6.3 16.8c1.4-2.6 3.6-4 5.7-4s4.3 1.4 5.7 4"/>
      <path d="M5.4 19.2h13.2"/>
    </template>

    <!-- gear: settings icon -->
    <template v-else-if="name === 'gear'">
      <circle cx="12" cy="12" r="2.8"/>
      <path d="M12 4.5v2.1M12 17.4v2.1M4.5 12h2.1M17.4 12h2.1M6.8 6.8l1.5 1.5M15.7 15.7l1.5 1.5M17.2 6.8l-1.5 1.5M8.3 15.7l-1.5 1.5"/>
    </template>

    <!-- search: magnifying glass -->
    <template v-else-if="name === 'search'">
      <circle cx="10.6" cy="10.6" r="5.8"/>
      <path d="m15.2 15.2 4 4"/>
    </template>

    <!-- plus: add icon -->
    <template v-else-if="name === 'plus'">
      <path d="M12 5.2v13.6M5.2 12h13.6"/>
    </template>

    <!-- refresh: refresh icon -->
    <template v-else-if="name === 'refresh'">
      <path d="M19 7.5V4.7h-2.8"/>
      <path d="M18.7 10.2a6.8 6.8 0 1 0 1 4.5"/>
    </template>

    <!-- card-money: loan card icon -->
    <template v-else-if="name === 'cardMoney'">
      <rect x="4" y="5.5" width="16" height="13" rx="2.8"/>
      <path d="M4 10.2h16"/>
      <path d="M16.8 14.6c.7-.7 1.1-1.6 1.1-2.6s-.4-2-1.1-2.6m-3.2 6c.6.5 1.4.8 2.2.8 1.8 0 3.2-1.4 3.2-3.2S17.6 9.8 15.8 9.8c-.8 0-1.6.3-2.2.8m2.2-1.7v8"/>
    </template>

    <!-- calendar: calendar icon -->
    <template v-else-if="name === 'calendar'">
      <rect x="4.5" y="6.2" width="15" height="13.4" rx="2.6"/>
      <path d="M8 4.8v3M16 4.8v3M4.5 9.6h15"/>
      <path d="M8.2 13.1h3.2M8.2 16.1h5.8"/>
    </template>

    <!-- mail: envelope icon -->
    <template v-else-if="name === 'mail'">
      <rect x="4" y="6.2" width="16" height="11.8" rx="2.6"/>
      <path d="m5.5 8 6.5 5 6.5-5"/>
    </template>

    <!-- user-circle: user avatar icon -->
    <template v-else-if="name === 'userCircle'">
      <circle cx="12" cy="12" r="9.2"/>
      <circle cx="12" cy="9.2" r="3"/>
      <path d="M6.6 17.3c1.4-2.4 3.5-3.7 5.4-3.7s4 1.3 5.4 3.7"/>
    </template>

    <!-- users: grouped people icon -->
    <template v-else-if="name === 'users'">
      <circle cx="9" cy="9.2" r="2.7"/>
      <circle cx="16.1" cy="10.2" r="2.3"/>
      <path d="M4.9 17.6c1.1-2.3 2.9-3.6 4.9-3.6s3.8 1.3 4.9 3.6"/>
      <path d="M13.2 17c.8-1.8 2.2-2.8 3.8-2.8 1.5 0 2.9 1 3.8 2.8"/>
    </template>

    <!-- check-card: approved document icon -->
    <template v-else-if="name === 'checkCard'">
      <rect x="4" y="5.5" width="16" height="13" rx="2.8"/>
      <path d="M4 10.2h16"/>
      <path d="m9.1 14.1 1.8 1.8 4-4.3"/>
    </template>

    <!-- check-calendar: calendar with check icon -->
    <template v-else-if="name === 'checkCalendar'">
      <rect x="4.5" y="6.2" width="15" height="13.4" rx="2.6"/>
      <path d="M8 4.8v3M16 4.8v3M4.5 9.6h15"/>
      <path d="m8.8 14 2 2 4.4-4.6"/>
    </template>

    <!-- lock: padlock icon -->
    <template v-else-if="name === 'lock'">
      <rect x="5.5" y="10.5" width="13" height="9.8" rx="2.4"/>
      <path d="M8.5 10.5V7.5a3.5 3.5 0 0 1 7 0v3"/>
      <path d="M12 14.5v2"/>
    </template>

    <!-- shield: security icon -->
    <template v-else-if="name === 'shield'">
      <path d="M12 3 4.5 6v5.5c0 4.4 3.2 8.5 7.5 9.5 4.3-1 7.5-5.1 7.5-9.5V6L12 3Z"/>
      <path d="m9 12 2 2 4-4"/>
    </template>

    <!-- document: file icon -->
    <template v-else-if="name === 'document'">
      <path d="M15 3H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-5-5Z"/>
      <path d="M15 3v6h6"/>
    </template>

    <!-- receipt: bill/statement icon -->
    <template v-else-if="name === 'receipt'">
      <path d="M7 3.8h10v16.4l-2-1.5-2 1.5-2-1.5-2 1.5-2-1.5-2 1.5V5.8A2 2 0 0 1 7 3.8Z"/>
      <path d="M9.2 8.4h5.6M9.2 11.8h5.6M9.2 15.2h3.4"/>
      <path d="M16.6 14.4c.7 0 1.2.4 1.2 1s-.5 1-1.2 1-1.2-.4-1.2-1 .5-1 1.2-1Zm0-2v5"/>
    </template>

    <!-- pie-chart: segmented circle -->
    <template v-else-if="name === 'pieChart'">
      <path d="M12 3.5a8.5 8.5 0 1 0 8.5 8.5H12Z"/>
      <path d="M12 3.5v8.5h8.5"/>
    </template>

    <!-- log: operation log icon -->
    <template v-else-if="name === 'log'">
      <path d="M9 12h6M9 8h6M9 16h4"/>
      <rect x="4" y="4" width="16" height="16" rx="2"/>
    </template>

    <!-- org: organization icon -->
    <template v-else-if="name === 'org'">
      <rect x="6" y="4" width="12" height="4" rx="1"/>
      <rect x="9" y="11" width="6" height="3" rx="1"/>
      <path d="M12 14v3M8 17h8"/>
      <path d="M6 8v3M18 8v3"/>
    </template>

    <!-- target: target/goal icon -->
    <template v-else-if="name === 'target'">
      <circle cx="12" cy="12" r="9"/>
      <circle cx="12" cy="12" r="5"/>
      <circle cx="12" cy="12" r="1"/>
    </template>

    <!-- trend: trending chart icon -->
    <template v-else-if="name === 'trend'">
      <path d="M22 7 13.5 15.5 8.5 10.5 2 17"/>
      <path d="M17 7h5v5"/>
    </template>

    <!-- calendarChart: bar chart icon -->
    <template v-else-if="name === 'calendarChart'">
      <rect x="4.5" y="6.2" width="15" height="13.4" rx="2.6"/>
      <path d="M8 4.8v3M16 4.8v3M4.5 9.6h15"/>
      <path d="M9 14.5v4M13 12v6.5M17 10.5v8"/>
    </template>

    <!-- performance: performance chart icon -->
    <template v-else-if="name === 'performance'">
      <path d="M5 18.5h14"/>
      <path d="M6 15h4M12.5 15V10M17 15V8"/>
    </template>

    <!-- salary: salary/wage icon -->
    <template v-else-if="name === 'salary'">
      <rect x="3" y="5" width="18" height="14" rx="2.8"/>
      <path d="M3 10h18M7 14h4M7 17h6"/>
    </template>

    <!-- income: income icon -->
    <template v-else-if="name === 'income'">
      <path d="M12 5v14M5 12l7-7 7 7"/>
    </template>

    <!-- expense: expense icon -->
    <template v-else-if="name === 'expense'">
      <path d="M12 19V5M5 12l7 7 7-7"/>
    </template>

    <!-- wallet: wallet icon -->
    <template v-else-if="name === 'wallet'">
      <rect x="3" y="6" width="18" height="13" rx="2.6"/>
      <path d="M3 10h18"/>
      <circle cx="16" cy="14" r="1.5"/>
    </template>

    <!-- bankCard: bank card icon -->
    <template v-else-if="name === 'bankCard'">
      <rect x="3" y="5" width="18" height="14" rx="2.8"/>
      <path d="M3 10h18"/>
      <path d="M7 15h2M11 15h2"/>
    </template>

    <!-- debt: debt icon -->
    <template v-else-if="name === 'debt'">
      <circle cx="12" cy="12" r="9"/>
      <path d="M12 8v4M12 14.5v.5"/>
      <path d="M9 12h6"/>
    </template>

    <!-- loan: loan icon -->
    <template v-else-if="name === 'loan'">
      <rect x="4" y="5.5" width="16" height="13" rx="2.8"/>
      <path d="M4 10.2h16"/>
      <path d="M8 14.5h3.2M8 17h2"/>
    </template>

    <!-- check-circle: check in circle -->
    <template v-else-if="name === 'checkCircle'">
      <circle cx="12" cy="12" r="9"/>
      <path d="m9 12 2 2 4-4"/>
    </template>

    <!-- star: star icon -->
    <template v-else-if="name === 'star'">
      <path d="m12 2 3 7h7l-5.5 4 2 7L12 16l-6.5 4 2-7L2 9h7z"/>
    </template>

    <!-- warning bell -->
    <template v-else-if="name === 'warningBell'">
      <path d="M12 4.5a4.1 4.1 0 0 0-4.1 4.1v2.1c0 .9-.3 1.7-.8 2.4L5.7 15h12.6l-1.4-1.9a4 4 0 0 1-.8-2.4V8.6A4.1 4.1 0 0 0 12 4.5Z"/>
      <path d="M10 18.2a2.3 2.3 0 0 0 4 0"/>
    </template>

    <!-- eye: visibility icon -->
    <template v-else-if="name === 'eye'">
      <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7-10-7-10-7Z"/>
      <circle cx="12" cy="12" r="3"/>
    </template>

    <!-- home: home icon -->
    <template v-else-if="name === 'home'">
      <path d="m3 12 7-8 7 8"/>
      <path d="M5 10v9a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-9"/>
    </template>

    <!-- chevron-down: dropdown arrow -->
    <template v-else-if="name === 'chevronDown'">
      <path d="m6 9 6 6 6-6"/>
    </template>

    <!-- arrow-right: arrow right icon -->
    <template v-else-if="name === 'arrowRight'">
      <path d="M5 12h14M12 5l7 7-7 7"/>
    </template>

    <!-- loading: loading spinner -->
    <template v-else-if="name === 'loading'">
      <path d="M12 3a9 9 0 0 1 9 9"/>
    </template>

    <!-- danger: danger icon -->
    <template v-else-if="name === 'danger'">
      <path d="M10.3 5.8 3 17.5h18L13.7 5.8a2 2 0 0 0-3.4 0Z"/>
      <path d="M12 10v3M12 15.5v.5"/>
    </template>

    <!-- warning: warning icon -->
    <template v-else-if="name === 'warning'">
      <path d="M10.3 5.8 3 17.5h18L13.7 5.8a2 2 0 0 0-3.4 0Z"/>
      <path d="M12 10v3M12 15.5v.5"/>
    </template>

    <!-- success: success check icon -->
    <template v-else-if="name === 'success'">
      <circle cx="12" cy="12" r="9"/>
      <path d="m9 12 2 2 4-4"/>
    </template>

    <!-- info: info icon -->
    <template v-else-if="name === 'info'">
      <circle cx="12" cy="12" r="9"/>
      <path d="M12 8h.01M12 11v4"/>
    </template>

    <!-- arrow-up: arrow up icon -->
    <template v-else-if="name === 'arrowUp'">
      <path d="M12 19V5M5 12l7-7 7 7"/>
    </template>

    <!-- arrow-down: arrow down icon -->
    <template v-else-if="name === 'arrowDown'">
      <path d="M12 5v14M5 12l7 7 7-7"/>
    </template>

    <!-- close: X icon -->
    <template v-else-if="name === 'close'">
      <path d="M18 6 6 18M6 6l12 12"/>
    </template>

    <!-- edit: pencil icon -->
    <template v-else-if="name === 'edit'">
      <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 4 21l.5-3.5Z"/>
    </template>

    <!-- delete: trash icon -->
    <template v-else-if="name === 'delete'">
      <path d="M5 7h14l-1.1 12H6.1Z"/>
      <path d="M20 7H4M9 7V4h6v3"/>
    </template>

    <!-- Fallback: question mark -->
    <template v-else>
      <circle cx="12" cy="12" r="9"/>
      <path d="M9 9a3 3 0 1 1 4 2.8v1"/>
      <path d="M12 16v1M12 19v.5"/>
    </template>
  </svg>
</template>
