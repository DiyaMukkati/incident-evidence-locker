import { createContext, useContext, useState } from "react";

// Auth Context — stores login state across the whole app
// Java Developer 2 — Day 5

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // Get token from localStorage if already logged in
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser]   = useState(
    JSON.parse(localStorage.getItem("user") || "null")
  );

  // Login — save token and user info
  const login = (tokenValue, userInfo) => {
    localStorage.setItem("token", tokenValue);
    localStorage.setItem("user", JSON.stringify(userInfo));
    setToken(tokenValue);
    setUser(userInfo);
  };

  // Logout — clear everything
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setToken(null);
    setUser(null);
  };

  const isLoggedIn = !!token;

  return (
    <AuthContext.Provider value={{ token, user, login, logout, isLoggedIn }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook to use auth anywhere in the app
export function useAuth() {
  return useContext(AuthContext);
}
