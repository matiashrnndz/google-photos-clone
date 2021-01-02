using System;
using System.Collections.Generic;

namespace DataAccess.Interface
{
    public interface IRepository<T> : IDisposable where T : class
    {
        T Add(T entity);

        void Remove(T entity);

        T Update(T entity);

        List<T> GetAll();

        T Get(Guid id);

        bool Exists(Guid id);

        void Save();
    }
}
