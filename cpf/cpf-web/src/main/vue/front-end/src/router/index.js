// import Vue from 'vue'
// import Router from 'vue-router'
import Layout from '@/components/Layout'
import Monitor from '@/components/Monitor'
import SmartWarn from '@/components/SmartWarn'
import WarnRule from '@/components/WarnRule'
import AlgorithmConfig from '@/components/AlgorithmConfig'
import SolutionConfig from '@/components/SolutionConfig'
import AlgorithmTable from '@/components/algorithmComp/AlgorithmTable'
import AlgorithmDetail from '@/components/algorithmComp/AlgorithmDetail'
import monitorChart from '@/components/monitorComp/monitorChart'
import monitorTable from '@/components/monitorComp/monitorTable'

// Vue.use(Router)

export default new VueRouter({
  routes: [
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      children:[{
        path: '/Monitor',
        component: Monitor,
        children:[{
          path: '',
          component: monitorTable,
        },{
          path: ':hostname',
          component: monitorChart,
        }]
      },{
        path: '/SmartWarn',
        component: SmartWarn,
      },{
        path: '/WarnRule',
        component: WarnRule,
      },{
        path: '/AlgorithmConfig',
        component: AlgorithmConfig,
        children: [
          {
            path: '',
            component: AlgorithmTable
          }, {
            path: ':id',
            component: AlgorithmDetail
          }]
      },{
        path: '/SolutionConfig',
        component: SolutionConfig,
      }]
    }
  ]
})
