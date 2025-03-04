const setItem = (key: string, value: any) => {
  sessionStorage.setItem(key, JSON.stringify(value));
}



const getItem = <T,> (key: string): T => {
  const valueStr = sessionStorage.getItem(key);
  return valueStr ? JSON.parse(valueStr) : null;
}

const delItem = (key: string) => {
  sessionStorage.removeItem(key);
}
const sessionUtils = {
  setItem,getItem,delItem
}
export default sessionUtils
