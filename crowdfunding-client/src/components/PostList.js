import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import AddComment from './AddComment';
import ViewComments from './ViewComments';

const PostList = ({ posts, onPostDeleted, onPostDonated, onCommentAdded }) => {
  const { username } = useAuth();
  const [activeCommentPost, setActiveCommentPost] = useState(null);
  const [viewingComments, setViewingComments] = useState(null);
  const [toDonate, setToDonate] = useState(1);

  const handleDelete = async (postId) => {
    try {
      await onPostDeleted(postId, toDonate);
    } catch (error) {
      console.error('Failed to delete post:', error);
    }
  };

  const handleDonate = async (postId) => {
    try {
      await onPostDonated(postId, toDonate);
    } catch (error) {
      console.error('Failed to donate to post:', error);
    }
  }

  const _setToDonate = (value) => {
    const intValue = parseInt(value);

    if (intValue >= 1) {
      setToDonate(value);
    }
  }

  return (
    <div>
      <h2>Последние благотворительные акции</h2>
      {posts.map(post => (
        <div key={post.id} className="card mb-3">
          <div className="card-body">
            <h4 className="card-title">
              <i className="bi bi-file-text me-2 text-primary"></i>
              {post.title}
            </h4>
            <h6>{post.description}</h6>
            <h4 className="card-money">
              Уже собрано {parseInt(post.moneyCurrent)}/{parseInt(post.moneyGoal)} ₽
            </h4>
            <div className="progress" style={{ height: "20px", width: "302px", marginBottom: "15px"}}>
            <div
              className="progress-bar bg-success"
              role="progressbar"
              style={{ width: `${(post.moneyCurrent / post.moneyGoal) * 100}%` }}
              aria-valuenow={post.moneyCurrent}
              aria-valuemin="0"
              aria-valuemax={post.moneyGoal}
            >
              {Math.round((post.moneyCurrent / post.moneyGoal) * 100)}%
            </div>
          </div>
            <input
              style={{ width: '160px', display: 'inline-block', marginRight: '10px' }}
              className="form-control"
              id="goal"
              type="number"
              min="1"
              value={toDonate}
              onChange={(e) => _setToDonate(e.target.value)}
              required
            />
            <button onClick={() => handleDonate(post.id)} className="btn btn-success me-2">Пожертвовать</button>
            <p className="card-text">{post.content}</p>
            <p className="card-text">
              <small className="text-muted">Создал {post.username} {new Date(post.createdAt).toLocaleDateString()}</small>
            </p>
            {username === post.username && (
              <button onClick={() => handleDelete(post.id)} className="btn btn-danger me-2">Удалить</button>
            )}
            <button onClick={() => setActiveCommentPost(post.id)} className="btn btn-primary me-2">Добавить комментарий</button>
            <button onClick={() => setViewingComments(post.id)} className="btn btn-secondary">Посмотреть комментарии</button>
            {activeCommentPost === post.id && (
              <AddComment postId={post.id} onCommentAdded={onCommentAdded} onClose={() => setActiveCommentPost(null)} />
            )}
            {viewingComments === post.id && (
              <ViewComments postId={post.id} onClose={() => setViewingComments(null)} />
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default PostList;