import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Common/Navbar";
import Home from "./pages/Home";
import Events from "./pages/Events";
import EventDetail from "./pages/EventDetail";
import Subscriptions from "./pages/Subscriptions";
import NotFound from "./pages/NotFound";
import LoginForm from "./components/Auth/LoginForm";
import SignupForm from "./components/Auth/SignupForm";
import ProtectedRoute from "./components/Common/ProtectedRoute";

const App: React.FC = () => (
    <AuthProvider>
        <Router>
            <Navbar />
            <div className="container">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/events" element={<Events />} />
                    <Route path="/events/:id" element={<EventDetail />} />
                    <Route path="/subscriptions" element={
                        <ProtectedRoute>
                            <Subscriptions />
                        </ProtectedRoute>
                    } />
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/signup" element={<SignupForm />} />
                    <Route path="*" element={<NotFound />} />
                </Routes>
            </div>
        </Router>
    </AuthProvider>
);

export default App;
