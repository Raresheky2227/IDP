import React, { createContext, useContext, useState, useEffect } from "react";
import { getUserInfoFromToken } from "../hooks/useAuth";

type UserInfo = {
    username: string;
    roles: string[];
};

type AuthContextType = {
    token: string | null;
    user: UserInfo | null;
    login: (token: string) => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
    token: null,
    user: null,
    login: () => {},
    logout: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [token, setToken] = useState<string | null>(() => localStorage.getItem("jwt") || null);
    const [user, setUser] = useState<UserInfo | null>(() =>
        token ? getUserInfoFromToken(token) : null
    );

    useEffect(() => {
        if (token) {
            localStorage.setItem("jwt", token);
            setUser(getUserInfoFromToken(token));
        } else {
            localStorage.removeItem("jwt");
            setUser(null);
        }
    }, [token]);

    const login = (tok: string) => setToken(tok);
    const logout = () => setToken(null);

    return (
        <AuthContext.Provider value={{ token, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuthContext = () => useContext(AuthContext);
