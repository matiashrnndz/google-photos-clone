using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace Domain
{
    public class Tag
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public Photo Photo { get; set; }

        public static Tag ByName(string name)
        {
            return new Tag()
            {
                Name = name
            };
        }
        
        public override bool Equals(object obj)
        {
            if (obj == null || GetType() != obj.GetType())
            {
                return false;
            }

            return ((Tag)obj).Name.Equals(Name);
        }
        
        public override int GetHashCode()
        {
            return new { Name }.GetHashCode();
        }
    }
}
