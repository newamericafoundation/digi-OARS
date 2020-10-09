import { useAuth } from "../../../auth-hook";


const Login = () => {
  const auth = useAuth();

  if (auth.isAuthenticated) {
      return null
  }
  return auth.login();
};

export default Login;
