import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './HomePage.css'; // Import the CSS file

const HomePage = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    isLogin: true,
  });
  const [error, setError] = useState('');
  const [totalAmount, setTotalAmount] = useState(0);
  const [categoryAmounts, setCategoryAmounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [transactionFormData, setTransactionFormData] = useState({
    type: '',
    category: null,
    amount: '',
    date: '',
  });
  const [editingTransaction, setEditingTransaction] = useState(null);

  var userId = localStorage.getItem('userId');
  const userApiBaseUrl = 'http://localhost:8082/api/users';
  const transactionApiBaseUrl = 'http://localhost:8081/api/transactions';
  const categoryApiBaseUrl = 'http://localhost:8081/api/categories';

  useEffect(() => {
    if (userId) {
      setIsAuthenticated(true);
      fetchData();
    }
  }, []);

  const fetchData = async () => {
    userId = localStorage.getItem("userId");
    try {
      const [totalRes, categoryRes, transactionsRes, categoriesRes] = await Promise.all([
        axios.get(`${transactionApiBaseUrl}/total/${userId}`),
        axios.get(`${transactionApiBaseUrl}/category/${userId}`),
        axios.get(`${transactionApiBaseUrl}/user/${userId}`),
        axios.get(categoryApiBaseUrl),
      ]);

      setTotalAmount(totalRes.data);
      setCategoryAmounts(categoryRes.data);
      setTransactions(
        transactionsRes.data.map((t) => ({
          ...t,
          date: new Date(t.date * 1000).toISOString().split('T')[0], // Convert epoch to YYYY-MM-DD
        }))
      );
      setCategories(categoriesRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (formData.password !== formData.confirmPassword && !formData.isLogin) {
        setError('Passwords do not match');
        return;
      }
      if (formData.isLogin) {
        const response = await axios.get(
          `${userApiBaseUrl}/login/${formData.email}/${formData.password}`
        );
        const { userId } = response.data.userId;
        localStorage.setItem('userId', response.data.userId);
        setIsAuthenticated(true);
        fetchData();
      } else {
        const response = await axios.post(userApiBaseUrl, {
          name: formData.name,
          email: formData.email,
          password: formData.password,
        });
        console.log(response.data);
        setFormData({
          ...formData,
          isLogin: true,
        });
      }
    } catch (err) {
      setError('Authentication error');
    }
  };

  const handleTransactionChange = (e) => {
    const { name, value } = e.target;
    setTransactionFormData((prevState) => ({
      ...prevState,
      [name]: name === 'category' ? categories.find((cat) => cat.categoryId === value) : value,
    }));
  };

  const handleTransactionSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...transactionFormData,
        user: { userId },
        category: transactionFormData.category,
        date: Math.floor(new Date(transactionFormData.date).getTime() / 1000), // Convert to epoch
      };
      if (editingTransaction) {
        await axios.put(`${transactionApiBaseUrl}/${editingTransaction.transactionId}`, payload);
      } else {
        await axios.post(transactionApiBaseUrl, payload);
      }
      setTransactionFormData({ type: '', category: null, amount: '', date: '' });
      setEditingTransaction(null);
      fetchData();
    } catch (error) {
      console.error('Error saving transaction:', error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`${transactionApiBaseUrl}/${id}`);
      fetchData();
    } catch (error) {
      console.error('Error deleting transaction:', error);
    }
  };

  const handleEdit = (transaction) => {
    setEditingTransaction(transaction);
    setTransactionFormData({
      type: transaction.type,
      category: transaction.category,
      amount: transaction.amount,
      date: transaction.date,
    });
  };

  if (!isAuthenticated) {
    return (
      <div className="home-page">
        <h1>FinTracker</h1>
        <div className="form-container">
  <h2>{formData.isLogin ? 'Login' : 'Register'}</h2>
  <form onSubmit={handleSubmit}>
    {!formData.isLogin && (
      <div>
        <label>Name</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>
    )}
    <div>
      <label>Email</label>
      <input
        type="email"
        name="email"
        value={formData.email}
        onChange={handleChange}
        required
      />
    </div>
    <div>
      <label>Password</label>
      <input
        type="password"
        name="password"
        value={formData.password}
        onChange={handleChange}
        required
      />
    </div>
    {!formData.isLogin && (
      <div>
        <label>Confirm Password</label>
        <input
          type="password"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleChange}
          required
        />
      </div>
    )}
    {error && <p className="error">{error}</p>}
    <button type="submit">{formData.isLogin ? 'Login' : 'Register'}</button>
  </form>
  <button
    className="toggle-form"
    onClick={() =>
      setFormData((prevState) => ({
        ...prevState,
        isLogin: !prevState.isLogin,
      }))
    }
  >
    {formData.isLogin ? 'Switch to Register' : 'Switch to Login'}
  </button>
</div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h1>Welcome to FinTracker</h1>
      <h2>Total Transaction Amount: ${totalAmount}</h2>
      <h3>Transaction Amount by Category</h3>
      <ul>
        {categoryAmounts.map((cat) => (
          <li key={cat.category.categoryId}>
            {cat.category.name}: ${cat.totalAmount}
          </li>
        ))}
      </ul>
      <h3>All Transactions</h3>
      <ul>
        {transactions.map((transaction) => (
          <li key={transaction.transactionId}>
            {transaction.type.toUpperCase()} - ${transaction.amount} - {transaction.category.name} -{' '}
            {transaction.date}
            <button onClick={() => handleEdit(transaction)}>Edit</button>
            <button onClick={() => handleDelete(transaction.transactionId)}>Delete</button>
          </li>
        ))}
      </ul>
      <form onSubmit={handleTransactionSubmit}>
        <h3>{editingTransaction ? 'Edit Transaction' : 'Add New Transaction'}</h3>
        <label>
          Type:
          <select
            name="type"
            value={transactionFormData.type}
            onChange={handleTransactionChange}
            required
          >
            <option value="">Select Type</option>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
          </select>
        </label>
        <label>
          Category:
          <select
            name="category"
            value={transactionFormData.category?.categoryId || ''}
            onChange={handleTransactionChange}
            required
          >
            <option value="">Select Category</option>
            {categories.map((cat) => (
              <option key={cat.categoryId} value={cat.categoryId}>
                {cat.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Amount:
          <input
            type="number"
            name="amount"
            value={transactionFormData.amount}
            onChange={handleTransactionChange}
            required
          />
        </label>
        <label>
          Date:
          <input
            type="date"
            name="date"
            value={transactionFormData.date}
            onChange={handleTransactionChange}
            required
          />
        </label>
        <button type="submit">{editingTransaction ? 'Update' : 'Add'} Transaction</button>
      </form>
    </div>
  );
};

export default HomePage;
