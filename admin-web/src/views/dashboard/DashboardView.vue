<template>
  <div class="dashboard">
    <section class="hero">
      <div>
        <p class="label">初始化完成</p>
        <h2>后台基础骨架已经就绪</h2>
        <p class="desc">
          这里将作为项目、人员、材料规则、回收审核、兑换审核和统计报表的入口。
        </p>
      </div>
      <el-tag type="success" size="large">ADM-BOOT</el-tag>
    </section>

    <section class="cards">
      <article class="card">
        <span>待开发模块</span>
        <strong>项目 / 班组 / 工人管理</strong>
      </article>
      <article class="card">
        <span>待开发模块</span>
        <strong>回收记录审核</strong>
      </article>
      <article class="card">
        <span>待开发模块</span>
        <strong>积分规则与兑换商品</strong>
      </article>
    </section>
  </div>
</template>

<style scoped lang="scss">
.dashboard {
  display: grid;
  gap: 18px;
}

.hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  border-radius: 24px;
  background:
    linear-gradient(135deg, rgba(34, 154, 110, 0.1), rgba(255, 255, 255, 0.94)),
    #fff;
  box-shadow: 0 16px 40px rgba(12, 45, 64, 0.08);
}

.label {
  margin: 0 0 10px;
  color: var(--adm-accent);
  font-size: 12px;
}

h2 {
  margin: 0 0 10px;
  font-size: 28px;
}

.desc {
  margin: 0;
  max-width: 720px;
  color: var(--adm-text-muted);
  line-height: 1.7;
}

.cards {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.card {
  display: grid;
  gap: 8px;
  padding: 20px;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(12, 45, 64, 0.06);
}

.card span {
  color: var(--adm-text-muted);
  font-size: 12px;
}

.card strong {
  font-size: 18px;
  color: var(--adm-text-main);
}

@media (max-width: 960px) {
  .hero {
    flex-direction: column;
  }

  .cards {
    grid-template-columns: 1fr;
  }
}
</style>
