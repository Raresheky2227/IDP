import React from "react";
import { useAuthContext } from "../../context/AuthContext";
import { Navigate, useLocation } from "react-router-dom";

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { token } = useAuthContext();
    const location = useLocation();
    if (!token) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }
    return <>{children}</>;
};

export default ProtectedRoute;
