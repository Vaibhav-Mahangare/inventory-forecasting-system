import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Restore session from localStorage on app start
    const savedToken = localStorage.getItem('inv_token');
    const savedUser = localStorage.getItem('inv_user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
      axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
    }
    setLoading(false);
  }, []);

  const login = (authResponse) => {
    const userData = {
      username: authResponse.username,
      email: authResponse.email,
      role: authResponse.role,
    };
    setToken(authResponse.token);
    setUser(userData);
    localStorage.setItem('inv_token', authResponse.token);
    localStorage.setItem('inv_user', JSON.stringify(userData));
    axios.defaults.headers.common['Authorization'] = `Bearer ${authResponse.token}`;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('inv_token');
    localStorage.removeItem('inv_user');
    delete axios.defaults.headers.common['Authorization'];
  };

  const isAdmin = () => user?.role === 'ADMIN';
  const isManager = () => user?.role === 'INVENTORY_MANAGER';
  const isAuthenticated = () => !!token && !!user;

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, isAdmin, isManager, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
