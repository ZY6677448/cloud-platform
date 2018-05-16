import fetch from '@/utils/fetch'





export const getAllRule = () => fetch('/config/rules');
export const deleteRuleByid = data => fetch('/config/rule', data, 'DELETE');
export const saveRule = data => fetch('/config/rule', data, 'POST');

export const getModelTypes = () => fetch('/config/modelType');
export const getScenes = () => fetch('/config/scenes');
export const getAllModel = () => fetch('/config/models');
export const getModel = data => fetch('/config/model', data);
export const deleteModelByid = data => fetch('/config/model', data, 'DELETE');
export const saveModel = data => fetch('/config/model', data, 'POST');


export const getAggreModel = data => fetch('/config/aggremodel', data);
export const getAllAggreModel = () => fetch('/config/aggremodels');
export const saveAggreModel = data => fetch('/config/aggremodel', data, 'POST');
export const deleteAggreModelByid = data => fetch('/config/aggremodel', data, 'DELETE');


export const getAVGData = data => fetch('/monitor/allAVG', data);

export const getAssets = (data) => fetch('/monitor/assets',data);
export const getChartData = data => fetch('/monitor/chartdata', data);

export const getAlarms = () => fetch('/alarms');