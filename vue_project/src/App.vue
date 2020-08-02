<template>
  <el-row type="flex" justify="center">
    <el-col :xs="24" :sm="22" :md="20" :lg="20" :xl="18">
      <router-view/>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
.el-table th.gutter{
  display: table-cell!important;
}
</style>
