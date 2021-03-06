import "./App.css";
import SummarizeView from "./view/SummarizeView.jsx";
import FailureView from "./view/FailureView.jsx";
import TransactionsView from "./view/TransactionsView.jsx";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Navbar, Container, Nav } from "react-bootstrap";

function App() {
  return (
    <Router>
      <div>
        <Navbar bg="dark" variant="dark">
          <Container>
            <Navbar.Brand href="/">
              <img
                alt=""
                src="/logo.svg"
                width="30"
                height="30"
                className="d-inline-block align-top"
              />{" "}
              Metrics App
            </Navbar.Brand>
            <Nav className="me-auto">
              <Nav.Link href="/">Summarized Metrics</Nav.Link>
              <Nav.Link href="/transactions">Transactions</Nav.Link>
              <Nav.Link href="/failures">Failures</Nav.Link>
            </Nav>
            <Navbar.Collapse className="justify-content-end">
              <Navbar.Text>
                By:{" "}
                <a
                  target="_blank"
                  rel="noreferrer"
                  href="https://alejandropardo.dev"
                >
                  Alejandro Pardo
                </a>
              </Navbar.Text>
            </Navbar.Collapse>
          </Container>
        </Navbar>
        <Routes>
          <Route path="/" element={<SummarizeView />} />
          <Route path="/transactions" element={<TransactionsView />} />
          <Route path="/failures" element={<FailureView />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
