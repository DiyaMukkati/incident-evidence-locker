import { useState } from "react";
import { AuthProvider, useAuth } from "./services/AuthContext";
import Login from "./pages/Login";
import IncidentList from "./pages/IncidentList";
import IncidentForm from "./pages/IncidentForm";
import "./index.css";

// Main App with simple navigation
// Java Developer 2 — Day 5

function AppContent() {
  const { isLoggedIn, user, logout } = useAuth();
  const [currentPage, setCurrentPage] = useState("list");

  // Show login if not logged in
  if (!isLoggedIn) {
    return <Login onLoginSuccess={() => setCurrentPage("list")} />;
  }

  return (
    <div className="min-h-screen bg-gray-100">

      {/* Navigation Bar */}
      <nav className="bg-blue-700 text-white px-6 py-4 shadow flex justify-between items-center">
        <h1 className="text-xl font-bold">🔒 Incident Evidence Locker</h1>
        <div className="flex items-center gap-6">
          <button
            onClick={() => setCurrentPage("list")}
            className={`text-sm hover:underline ${currentPage === "list" ? "font-bold" : ""}`}
          >
            All Incidents
          </button>
          <button
            onClick={() => setCurrentPage("create")}
            className={`text-sm hover:underline ${currentPage === "create" ? "font-bold" : ""}`}
          >
            + New Incident
          </button>
          <span className="text-sm opacity-75">
            👤 {user?.username}
          </span>
          <button
            onClick={logout}
            className="text-sm bg-blue-800 px-3 py-1 rounded hover:bg-blue-900"
          >
            Logout
          </button>
        </div>
      </nav>

      {/* Page Content */}
      <main className="max-w-7xl mx-auto py-6 px-4">
        {currentPage === "list" && (
          <IncidentList />
        )}
        {currentPage === "create" && (
          <IncidentForm onSuccess={() => setCurrentPage("list")} />
        )}
      </main>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
