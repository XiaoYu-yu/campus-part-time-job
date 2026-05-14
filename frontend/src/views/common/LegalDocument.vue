<template>
  <div class="legal-page">
    <header class="legal-header">
      <button type="button" @click="goBack">返回</button>
      <div>
        <span>校内兼职平台</span>
        <h1>{{ documentTitle }}</h1>
      </div>
    </header>

    <main class="legal-card">
      <section v-for="section in sections" :key="section.title">
        <h2>{{ section.title }}</h2>
        <p v-for="item in section.items" :key="item">{{ item }}</p>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const isPrivacy = computed(() => route.name === 'PrivacyPolicy')
const documentTitle = computed(() => isPrivacy.value ? '隐私政策' : '用户协议')

const privacySections = [
  {
    title: '我们会收集哪些信息',
    items: [
      '账号信息：手机号、昵称或姓名，用于登录和识别账号。',
      '订单信息：取餐点、送达楼栋、联系人、手机号、备注、订单状态和模拟支付结果，用于完成校园代送。',
      '兼职资料：学号、学院、宿舍楼、紧急联系人、审核材料，用于判断是否具备接单资格。',
      '异常与反馈：异常说明、售后备注、反馈内容和联系方式，用于处理问题和改进服务。'
    ]
  },
  {
    title: '信息怎么使用',
    items: [
      '用于创建订单、分配兼职人员、展示进度、处理异常和售后。',
      '用于平台运营、安全审计、问题定位和内测反馈处理。',
      '当前项目不接真实支付，不做真实退款，不做真实打款。'
    ]
  },
  {
    title: '保存与保护',
    items: [
      '内测期间数据保存在项目服务器和数据库中，仅用于本项目调试、演示和试运营。',
      '不会主动向无关第三方出售或共享你的个人信息。',
      '如需要删除、更正或导出信息，可通过 App 内反馈入口联系处理。'
    ]
  }
]

const termsSections = [
  {
    title: '服务说明',
    items: [
      '本平台用于校内代送需求发布、兼职人员接单、订单进度查看和异常反馈。',
      '当前属于试运营版本，支付、退款、打款和地图等能力仍可能使用模拟或有限能力。',
      '使用过程中请填写真实、必要、准确的信息，避免影响取餐和送达。'
    ]
  },
  {
    title: '用户责任',
    items: [
      '不得发布违法、骚扰、虚假或与校园代送无关的内容。',
      '兼职人员应按实际情况接单和反馈异常，不得冒领、恶意拖延或泄露他人信息。',
      '遇到订单异常时，请优先通过页面内异常、售后或反馈入口说明情况。'
    ]
  },
  {
    title: '试运营限制',
    items: [
      '平台仍处于内测到试运营过渡阶段，可能出现功能调整、数据重置或服务中断。',
      '本项目不会在当前阶段承诺真实支付结算能力。',
      '继续使用即表示你理解并接受本试运营边界。'
    ]
  }
]

const sections = computed(() => isPrivacy.value ? privacySections : termsSections)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push('/user/login')
}
</script>

<style scoped lang="scss">
.legal-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding: calc(18px + env(safe-area-inset-top, 0px)) 16px calc(24px + env(safe-area-inset-bottom, 0px));
  background: linear-gradient(180deg, #f0fdfa 0%, #f8fafc 38%, #ffffff 100%);
  color: #18181b;
}

.legal-header {
  display: flex;
  gap: 14px;
  align-items: center;
  margin: 0 auto 16px;
  max-width: 520px;

  button {
    border: 1px solid #d4d4d8;
    background: #ffffff;
    border-radius: 999px;
    padding: 8px 12px;
    color: #0f766e;
    font-weight: 700;
  }

  span {
    color: #0f766e;
    font-size: 12px;
    font-weight: 800;
  }

  h1 {
    margin: 4px 0 0;
    font-size: 24px;
  }
}

.legal-card {
  max-width: 520px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);

  section + section {
    margin-top: 20px;
    padding-top: 18px;
    border-top: 1px solid #f1f5f9;
  }

  h2 {
    margin: 0 0 10px;
    font-size: 17px;
  }

  p {
    margin: 8px 0;
    color: #52525b;
    line-height: 1.8;
    font-size: 14px;
  }
}
</style>
