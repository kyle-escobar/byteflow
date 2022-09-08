# All files in the distribution of BLOAT (Bytecode Level Optimization and
# Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
# Research Foundation of Purdue University.  All rights reserved.
# 
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA


SUBDIRS = util \
	  reflect \
	  file \
	  editor \
	  inline \
	  tree \
	  cfg \
	  ssa \
	  tbaa \
	  trans \
	  diva \
	  codegen \
	  context \
	  decorate \
	  optimize \
	  shrink \
	  strip \
	  dump \
	  tools \
	  benchmark

all:
	@for i in $(SUBDIRS) ""; do \
	    if [ "x$$i" != "x" ]; then \
		$(MAKE) -C $$i all; \
	    fi; \
	done

clean:
	@for i in $(SUBDIRS) ""; do \
	    if [ "x$$i" != "x" ]; then \
		$(MAKE) -C $$i clean; \
	    fi; \
	done

docs:
	javadoc -d ../../../../../docs -sourcepath ../../../.. \
	  dev.kyleescobar.byteflow.util \
	  dev.kyleescobar.byteflow.reflect \
	  dev.kyleescobar.byteflow.file \
	  dev.kyleescobar.byteflow.editor \
	  dev.kyleescobar.byteflow.tree \
	  dev.kyleescobar.byteflow.cfg \
	  dev.kyleescobar.byteflow.ssa \
	  dev.kyleescobar.byteflow.tbaa \
	  dev.kyleescobar.byteflow.trans \
	  dev.kyleescobar.byteflow.diva \
	  dev.kyleescobar.byteflow.codegen \
	  dev.kyleescobar.byteflow.context \
	  dev.kyleescobar.byteflow.decorate \
	  dev.kyleescobar.byteflow.optimize \
	  dev.kyleescobar.byteflow.shrink \
	  dev.kyleescobar.byteflow.strip \
	  dev.kyleescobar.byteflow.dump \
	  dev.kyleescobar.byteflow.benchmark
