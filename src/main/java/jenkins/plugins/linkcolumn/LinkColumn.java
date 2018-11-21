/*
 * The MIT License
 *
 * Copyright (c) 2018 - present, Karl Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package jenkins.plugins.linkcolumn;

import hudson.EnvVars;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

public class LinkColumn extends ListViewColumn {

    private String columnName;
    private String linkName;
    private String link;

    @DataBoundConstructor
    public LinkColumn(String columnName, String linkName, String link) {
        super();
        this.columnName = columnName;
        this.linkName = linkName;
        this.link = link;
    }


    public String getColumnName() {
        return columnName;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLink() {
        return link;
    }


    public String getTokenizedLinkName(Job<?, ?> job) {
        return tokenize(linkName, job);
    }

    public String getTokenizedLink(Job<?, ?> job) {
        return tokenize(link, job);
    }

    private String tokenize(String value, Job<?, ?> job) {
        Run<?, ?> lastBuild = job.getLastBuild();
        String rootUrl = Jenkins.getInstance().getRootUrl();
        String jobName = job.getName();
        String jobUrl = rootUrl + job.getShortUrl();
        
        String buildNumber = "null";
        String buildUrl = "null";
        String buildStatus = "null";
        if (lastBuild != null) {
            buildNumber = String.valueOf(lastBuild.getNumber());
            buildUrl = rootUrl + job.getShortUrl() + buildNumber + "/";
            buildStatus = lastBuild.getResult() != null ? lastBuild.getResult().toString() : "null";
        }
        
        EnvVars envVars = new EnvVars();
        envVars.put("ROOT_URL", rootUrl);
        envVars.put("JOB_NAME", jobName);
        envVars.put("JOB_URL", jobUrl);
        envVars.put("BUILD_NUMBER", buildNumber);
        envVars.put("BUILD_URL", buildUrl);
        envVars.put("BUILD_STATUS", buildStatus);

        return envVars.expand(value);
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        public DescriptorImpl() {
            Items.XSTREAM2.addCompatibilityAlias("hudson.views.LinkColumn", LinkColumn.class);
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Custom Link";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/link-column/link-column.html";
        }
    }

}
