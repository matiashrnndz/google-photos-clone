using Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebApi.Models
{
    public class TagModel : Model<Tag, TagModel>
    {
        public Guid Id { get; set; }
        public string Name { get; set; }

        public TagModel() { }

        public TagModel(Tag entity)
        {
            SetModel(entity);
        }

        public override Tag ToEntity()
        {
            Tag photo = new Tag()
            {
                Id = this.Id,
                Name = this.Name
            };
            return photo;
        }

        protected override TagModel SetModel(Tag entity)
        {
            Id = entity.Id;
            Name = entity.Name;
            return this;
        }
    }
}
